package com.shenmao.chuhe.handlers;

import com.shenmao.chuhe.commons.checkcode.CheckCodeGen;
import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.database.wikipage.WikiPageDbService;
import com.shenmao.chuhe.exceptions.PurposeException;
import com.shenmao.chuhe.passport.RealmImpl;
import com.shenmao.chuhe.serialization.SerializeType;
import com.shenmao.chuhe.passport.AuthHandlerImpl;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.rxjava.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.FileUpload;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import rx.Single;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public class PortalHandlers extends BaseHandler {

  private static final Logger logger = LoggerFactory.getLogger(PortalHandlers.class);

  public static PortalHandlers create(ChuheDbService chuheDbService, WikiPageDbService wikiPageDbService) {
    return new PortalHandlers(chuheDbService, wikiPageDbService);
  }

  ChuheDbService chuheDbService;
  WikiPageDbService wikiPageDbService;

  public PortalHandlers(ChuheDbService chuheDbService, WikiPageDbService wikiPageDbService) {
    this.chuheDbService = chuheDbService;
    this.wikiPageDbService = wikiPageDbService;
  }

  public void indexHandler(RoutingContext routingContext) {

    if (routingContext.user() == null) {
      routingContext.response().setStatusCode(302).putHeader("Location", "/login").end();
      return;
    }

    ChainSerialization.create(routingContext.getDelegate())
      .putViewName("/index.html")
      .putContextData(null)
      .serialize();
  }


  public void checkCodeHandler(RoutingContext routingContext) {

    String sign = getQueryParam(routingContext, "sign");

    if ( sign == null || sign.isEmpty()) {
      throw new PurposeException("非法请求");
    }

    final CheckCodeGen checkCode = CheckCodeGen.createCheckCode();

    JsonObject code  = new JsonObject();

    code.put("code_sign", sign);
    code.put("code_value", checkCode.getCheckCodeStr());
    code.put("send_channel", "email");

    this.chuheDbService.createCheckCode(code, reply -> {

      if (reply.succeeded()) {
        ByteArrayOutputStream baos = checkCode.createImgStream();

        routingContext.getDelegate().response().putHeader("Content-Length",String.valueOf(baos.size()));
        routingContext.getDelegate().response().write(Buffer.buffer(baos.toByteArray()));
        routingContext.getDelegate().response().end();
        return;
      }

      routingContext.getDelegate().response().end(reply.cause().getMessage());

    });

  }


  public void accessToken(RoutingContext routingContext) {

    String username = null;
    String password = null;

    try {
      JsonObject jsonObject = routingContext.getBodyAsJson();
      username = jsonObject.getString("username");
      password = jsonObject.getString("password");
    } catch (Exception e) {
      username = routingContext.request().getParam("username");
      password = routingContext.request().getParam("password");
    }

    JsonObject jsonObject = new JsonObject()
      .put("username", username)
      .put("password", password);

    AuthHandlerImpl.getAuthProvider().rxAuthenticate(jsonObject)
            .flatMap(user -> RealmImpl.userDetail(user))
            .subscribe(userObject -> {
      JWTOptions jwtOptions = new JWTOptions().setSubject("Wiki API").setIssuer("Vert.x");
      String token = AuthHandlerImpl.getJWTAuthProvider().generateToken(userObject, jwtOptions);
      ChainSerialization.create(routingContext.getDelegate())
              .setSerializeType(SerializeType.JSON)
              .putContextData(token)
              .serialize();
    }, err -> routingContext.fail(401));

  }

  public void registry(RoutingContext routingContext) {

    JsonObject contextData = new JsonObject();

    ChainSerialization.create(routingContext.getDelegate())
            .putViewName("/registry.html")
            .putContextData(contextData)
            .serialize();
  }

  public void userRegistry(RoutingContext routingContext) {

    JsonObject user = new JsonObject();
    JsonArray roles = new JsonArray();

    user.put("user_name", getString(routingContext, "user_name"));
    user.put("user_passwd", getString(routingContext, "user_passwd"));
    roles.add("role_user");


    ChainSerialization chainSerialization =
            ChainSerialization.create(routingContext.getDelegate());

    this.chuheDbService.createUser(user, roles, reply -> {

      if (reply.succeeded()) {
        chainSerialization
                .putContextData(reply.result())
                .putFlashMessage("注册成功")
                .redirect("/login");
      } else {
        chainSerialization
                .putFlashMessage(reply.cause().getMessage())
                .putMessage(reply.cause().getMessage())
                .putException(reply.cause())
                .putFlashException(reply.cause())
                .redirect("/registry");
      }

    });


  }

  public void login(RoutingContext routingContext) {

    JsonObject contextData = new JsonObject();

    ChainSerialization.create(routingContext.getDelegate())
      .putViewName("/login.html")
      .putContextData(contextData)
      .serialize();
  }

  public void logout(RoutingContext routingContext) {

    if (routingContext.user() != null) {
      routingContext.clearUser();
      routingContext.session().remove("userDetail");
    }

    routingContext.response().setStatusCode(302).putHeader("Location", "/").end();
  }

  public void catalogue(RoutingContext routingContext) {
    String productType = routingContext.request().getParam("param0");
    String productId = routingContext.request().getParam("param1");
    routingContext.response().putHeader("Content-Type", "text/html;charset=UTF-8");
    routingContext.response().end("productType: <b>"+productType+"</b> <br/>productId: <b>"+productId+"</b> ");
  }


  public void articles(RoutingContext routingContext) {

    String productType = routingContext.request().getParam("param0");
    String productId = routingContext.request().getParam("param1");

    JsonObject contextData = new JsonObject()
      .put("productType", productType)
      .put("productId", productId);

    ChainSerialization.create(routingContext.getDelegate())
      .putViewName("/articles/article_detail.html")
      .putContextData(contextData)
      .serialize();

  }

  public void uploadFiles(RoutingContext routingContext) {
    Set<FileUpload> uploads = routingContext.getDelegate().fileUploads();
    uploads.stream().forEach( f -> {
      System.out.println(f.name() + ", ddd 1");
      System.out.println(f.fileName() + ", ddd 2");
    });
    System.out.println(routingContext.request().getParam("userid") + " 3");
    System.out.println(routingContext.request().getParam("filecomment") + " 4");
    routingContext.response().end("<" + uploads.size() + "> files uploaded");
  }

  public void dashboard(RoutingContext routingContext) {
    JsonObject contextData = new JsonObject();

    ChainSerialization.create(routingContext.getDelegate())
      .putViewName("/dashboard/dashboard_index.html")
      .putContextData(contextData)
      .serialize();
  }

  public void chatRoom(RoutingContext routingContext) {

    ChainSerialization.create(routingContext.getDelegate())
      .putViewName("/chat_room/chat_room_index.html")
      .putContextData(new JsonObject())
      .serialize();
  }

  public void wikiPage(RoutingContext routingContext) {

    wikiPageDbService.fetchAllPages( reply -> {

      if (reply.succeeded()) {


        List<JsonObject> wikiPageList = reply.result();

        ChainSerialization.create(routingContext.getDelegate())
          .putViewName("/wiki_page/wiki_page_index.html")
          .putContextData(wikiPageList)
          .serialize();

      } else {
        logger.error(reply.cause().getMessage());
      }

    });


  }

  public void sendMessage(RoutingContext routingContext) {

    String message = routingContext.getBodyAsJson().getString("message");

    JsonObject contextData = new JsonObject()
      .put("username", routingContext.user().principal().getString("username"))
      .put("message", message )
      .put("dateTime", (new Timestamp(System.currentTimeMillis())).getTime());

    ChainSerialization.create(routingContext.getDelegate())
      .setSerializeType(SerializeType.WS)
      .setWsEndpoint("chat_room.1")
      .putContextData(contextData)
      .serialize();

  }


  public SockJSHandler eventBusHandler(Vertx vertx, String endpoint) {

    BridgeOptions options = new BridgeOptions()
      .addOutboundPermitted(new PermittedOptions().setAddressRegex(endpoint));

    Handler<BridgeEvent> bridgeEventHandler = event -> {

      /* if (event.type() == BridgeEventType.SOCKET_CREATED) {
        logger.info("A socket was created");
      } */

        /* JsonObject rawMessage = event.getRawMessage();

        // put some headers
        event.setRawMessage(rawMessage);

        JsonObject body = new JsonObject(rawMessage.getString("body"));

        // update body and send to client
        // TODO

        event.socket().write(rawMessage.encode()); */

      /* if (event.type() != BridgeEventType.SOCKET_PING) {
        System.out.println(event.type() + ", event.type()");
      } */

      if (event.type() == BridgeEventType.SOCKET_CLOSED) {
        // TODO
      }

      event.complete(true);

    };

    return SockJSHandler.create(vertx).bridge(options, bridgeEventHandler);

  }

}
