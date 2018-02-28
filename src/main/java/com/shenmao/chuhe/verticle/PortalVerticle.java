package com.shenmao.chuhe.verticle;

import com.shenmao.chuhe.routers.ChuheRouter;
import com.shenmao.chuhe.routers.GlobalRouter;
import com.shenmao.chuhe.routers.ManageRouter;
import com.shenmao.chuhe.routers.PortalRouter;
import com.shenmao.chuhe.sessionstore.RedisSessionStore;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.FileUpload;
import io.vertx.rxjava.ext.web.handler.*;
import io.vertx.rxjava.ext.web.sstore.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.core.AbstractVerticle;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import com.shenmao.chuhe.Application;

public class PortalVerticle extends AbstractVerticle {

  private static final int KB = 1024;
  private static final int MB = 1024 * KB;
  private static final int _PORT = 8081;

  private static final Logger logger = LoggerFactory.getLogger(PortalVerticle.class);

  @Override
  public void start() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {


    Router router = Router.router(vertx);



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

    // file upload and body parameters parser
    router.route()
            .handler(BodyHandler.create()
                    .setBodyLimit(50 * MB)
                    .setMergeFormAttributes(true)
                    .setUploadsDirectory(Application.UPLOAD_FOLDER))
            .handler(Application::methodOverride)
            .handler(Application::remoteUploadedEmptyFile);

    router.route().handler(SessionHandler.create(sessionStore).setCookieHttpOnlyFlag(false));
    //.setCookieSecureFlag(true));         // Session session = routingContext.session(); session.put("foo", "bar");
    //router.route().handler(CSRFHandler.create("not a good secret"));

    // Serving static resources
    router.route("/venders/*").handler(StaticHandler.create("static/venders").setCachingEnabled(false).setIncludeHidden(true).setDirectoryListing(true));
    router.route("/angular/*").handler(StaticHandler.create("static/angular").setCachingEnabled(false).setIncludeHidden(true).setDirectoryListing(true));
    router.route("/scripts/*").handler(StaticHandler.create("static/scripts").setCachingEnabled(false).setIncludeHidden(true).setDirectoryListing(true));
    router.route("/assets/*").handler(StaticHandler.create("static/assets").setCachingEnabled(false).setIncludeHidden(true).setDirectoryListing(true));
    router.route("/styles/*").handler(StaticHandler.create("static/styles").setCachingEnabled(false).setIncludeHidden(true).setDirectoryListing(true));
    router.route("/images/*").handler(StaticHandler.create("static/images").setCachingEnabled(false).setIncludeHidden(true).setDirectoryListing(true));
    router.route("/uploads/*").handler(StaticHandler.create("static/uploads").setCachingEnabled(false).setIncludeHidden(true).setDirectoryListing(true));

    // router.route().handler(TimeoutHandler.create(50000));
    // router.route().handler(VertxExceptionHandler.create(5000));
    // router.route().failureHandler(ErrorHandler.create(true));

    vertx.exceptionHandler(new Handler<Throwable>() {
      @Override
      public void handle(Throwable throwable) {
        /* System.out.println(throwable.getMessage() + ",,, vertx.exceptionHandler");
           logger.error(throwable.getMessage()); */
      }

    });

    router.mountSubRouter("/", ChuheRouter.create(PortalRouter.class.getName(), vertx).getRouter());
    router.mountSubRouter("/mans/", ChuheRouter.create(ManageRouter.class.getName(), vertx).getRouter());
    router.mountSubRouter("/", ChuheRouter.create(GlobalRouter.class.getName(), vertx).getRouter());

    vertx.createHttpServer().requestHandler(router::accept).listen(_PORT);

  }

}
