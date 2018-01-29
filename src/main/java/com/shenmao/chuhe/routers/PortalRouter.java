package com.shenmao.chuhe.routers;

import com.shenmao.chuhe.database.wikipage.WikiPageDbService;
import com.shenmao.chuhe.handlers.GlobalHandlers;
import com.shenmao.chuhe.handlers.PortalHandlers;
import com.shenmao.chuhe.sessionstore.RedisSessionStore;
import com.shenmao.chuhe.exceptions.PurposeException;
import com.shenmao.chuhe.passport.AuthHandlerImpl;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.ext.web.Route;
import io.vertx.rxjava.ext.web.sstore.SessionStore;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.*;

import static com.shenmao.chuhe.verticle.WikiPageVerticle.CONFIG_WIKIDB_QUEUE;

public class PortalRouter  implements ChuheRouter {

  private static final int KB = 1024;
  private static final int MB = 1024 * KB;

  public static PortalRouter create(Vertx vertx, AuthHandler authHandler) {
    return new PortalRouter(vertx, authHandler);
  }

  Vertx vertx;
  Router router;

  AuthHandler authHandler = null;
  GlobalHandlers globalHandlers = null;
  PortalHandlers defaultHandlers = null;
  WikiPageDbService wikiPageDbService = null;

  public PortalRouter(Vertx vertx, AuthHandler authHandler) {

    this.vertx = vertx;
    this.router = Router.router(vertx);
    this.authHandler = authHandler;
    wikiPageDbService = WikiPageDbService.createProxy(vertx.getDelegate(), CONFIG_WIKIDB_QUEUE);
    globalHandlers = GlobalHandlers.create();
    defaultHandlers = PortalHandlers.create(wikiPageDbService);

    this.init();

  }

  private void init() {

    this.wrapRouterHandler(vertx, this.router);

    /***** routers *****/
    this.router.route("/eventbus/*").handler(defaultHandlers.eventBusHandler(vertx, "chat_room\\.[0-9]+"));

    // 基于权限验证的 router
    this.router.route("/dashboard/*").handler(authHandler);
    this.router.route("/chat-room/*").handler(authHandler);

    this.indexRouter();
    this.pingRouter();
    this.mainRouter();
    this.labelRouter();
    this.fileUploadRouter();
    this.chatRoomRouter();
    this.wikiPageRouter();
    this.dashboardRouter();

  }

  private void indexRouter() {

    // 首页
    this.router.route(HttpMethod.GET, "/").handler(routingContext ->
            routingContext.reroute("/index"));
    this.router.routeWithRegex(HttpMethod.GET, "/index" + GlobalHandlers._SUPPORT_EXTS_PATTERN).handler(defaultHandlers::indexHandler);

  }

  private void pingRouter() {

    // curl -v -X POST http://localhost:8081/ping
    Route routePingPost =  this.router.route(HttpMethod.POST, "/ping");
    Route routePingGet =  this.router.routeWithRegex(HttpMethod.GET, "/ping|/");

    routePingGet.handler(routingContext -> routingContext.fail(406));
    routePingPost.handler(defaultHandlers::indexHandler);

  }

  private void mainRouter() {

    this.router.route(HttpMethod.GET, "/signout").handler(defaultHandlers::logout);
    this.router.routeWithRegex(HttpMethod.POST, "/login-auth").handler(CSRFHandler.create("csrf secret here"));
    this.router.route(HttpMethod.GET, "/login")
      .handler(CSRFHandler.create("csrf secret here"))
      .handler(defaultHandlers::login);

    // JWT access token
    // curl -v -X POST -F "username=keesh" -F "password=keesh" http://localhost:8081/access_token
    // curl -v -X POST -H "Content-Type:application/x-www-form-urlencoded" -d '{"username": "keesh", "password": "keesh"}' http://localhost:8081/access_token
    // curl -v -X POST -H "Content-Type:application/json" -d '{"username": "keesh", "password": "keesh"}' http://localhost:8081/access_token
    this.router.routeWithRegex(HttpMethod.POST, "/access_token" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
      .handler(defaultHandlers::accessToken);

    this.router.routeWithRegex(HttpMethod.POST,"/login-auth" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
      .handler(FormLoginHandler.create(AuthHandlerImpl.getAuthProvider(),
        "username", "password", "return_url", "/"));

  }

  private void labelRouter() {

    // 基于路径参数的 router
    //Route routeProduct = this.router.route(HttpMethod.GET, "/catalogue/products/:productType/:productId");
    this.router
      .routeWithRegex(HttpMethod.GET, "/catalogue/products/(?<productType>[^\\/.]+)/(?<productId>[^\\/.]+)" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
      .handler(defaultHandlers::catalogue);

    // 基于正则表达式的 router
    // Route routeArticle = this.router.routeWithRegex(HttpMethod.GET, "/articles/channel/(?<articleType>[^\\/]+)/(?<articleTag>[^\\/]+)(.json|.xml)?");
    this.router
      .routeWithRegex(HttpMethod.GET, "/articles/channel/(?<articleType>[^\\/.]+)/(?<articleTag>[^\\/.]+)" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
      .handler(defaultHandlers::articles);

  }

  private void fileUploadRouter() {
    /* curl \
    -F "userid=6887" \
    -F "filecomment=This is an image file" \
    -F "image=@/home/keesh/Desktop/timg.jpg" http://localhost:8081/file_uploads */
    this.router.routeWithRegex(HttpMethod.POST, "/file_uploads" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
      .handler(defaultHandlers::uploadFiles);
  }

  private void chatRoomRouter() {

    this.router.route("/chat-room").handler(defaultHandlers::chatRoom);

    this.router.route(HttpMethod.POST, "/chat-room/send-message")
      .consumes("*/json").handler(routingContext -> {
      /* JsonObject j1 = new JsonObject().put("username", "u1").put("message", "6ttt").put("dateTime", 1516180321963L);
      JsonObject j2 = new JsonObject().put("code", 200).put("message", "OK").put("data", j1);
      vertx.eventBus().publish("chat_room.1", j2); */
      defaultHandlers.sendMessage(routingContext);
    });

  }

  private void wikiPageRouter() {

    this.router.route("/wiki-page").handler(defaultHandlers::wikiPage);
  }

  private void dashboardRouter() {
    // curl -u keesh:keesh http://localhost:8081/dashboard
    this.router.route("/dashboard").handler(defaultHandlers::dashboard);
  }

  public void wrapRouterHandler(Vertx vertx, Router router) {

//    SessionStore sessionStore = LocalSessionStore.create(vertx, "myapp.sessionmap", 10000);
    SessionStore sessionStore = new SessionStore(RedisSessionStore
      .create(vertx.getDelegate(), "myapp.sessionmap", 10000)
      .host("127.0.0.1").port(6379));

    // Route: consumes and produces
    // routeInstance.consumes("text/html").consumes("text/plain").consumes("*/json");
    // routeInstance.produces("application/json");
    // etc..

    // vertxRouter.route().handler(CorsHandler.create("vertx\\.io").allowedMethod(HttpMethod.GET));
    router.route().handler(ResponseTimeHandler.create());
    // vertxRouter.route().handler(ResponseContentTypeHandler.create());
    router.route().handler(FaviconHandler.create());
    router.route().handler(CookieHandler.create());                      // Cookie cookie = routingContext.getCookie("cookie name here")
    router.route().handler(BodyHandler.create().setBodyLimit(50 * MB).setUploadsDirectory("uploads"));                        // file upload and body parameters parser
    router.route().handler(SessionHandler.create(sessionStore).setCookieHttpOnlyFlag(false));
    //.setCookieSecureFlag(true));         // Session session = routingContext.session(); session.put("foo", "bar");
    //router.route().handler(CSRFHandler.create("not a good secret"));
    router.route().handler(UserSessionHandler.create(AuthHandlerImpl.getAuthProvider()));
    router.route().failureHandler(routingContext ->  {

      if (routingContext.failure() instanceof VertxException
        && routingContext.failure().getMessage().equals("Connection was closed")) {
        return;
      }

      globalHandlers.exceptionHandler(routingContext);

    });

    router.route().handler(routingContext -> {
      // middlewares here
      routingContext.next();
    });

    // default Serving
    router.route().handler(routingContext -> {
      globalHandlers.htmlRenderHandler(routingContext);
    });

    // Serving .html .json and .xml
    router.routeWithRegex(".+\\.html").handler(globalHandlers::htmlRenderHandler);
    router.routeWithRegex(".+\\.json").handler(globalHandlers::jsonSerializationHandler);
    router.routeWithRegex(".+\\.xml").handler(globalHandlers::xmlSerializationHandler);

    // not support exts
    router.route().handler(globalHandlers::notSupportExtensionMiddleware);

    // tester
    router.routeWithRegex(HttpMethod.GET, "/pip" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
      .handler(routingContext -> routingContext.put("count", 1).next())
      .handler(routingContext -> routingContext.response().end("pipline: " + ((int)routingContext.get("count") + 1)));

  }

  public Router getRouter() {
    return router;
  }

}
