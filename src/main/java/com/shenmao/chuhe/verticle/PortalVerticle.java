package com.shenmao.chuhe.verticle;

import com.shenmao.chuhe.passport.AuthHandlerImpl;
import com.shenmao.chuhe.routers.ChuheRouter;
import com.shenmao.chuhe.routers.GlobalRouter;
import com.shenmao.chuhe.routers.ManageRouter;
import com.shenmao.chuhe.routers.PortalRouter;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.handler.StaticHandler;

import java.lang.reflect.InvocationTargetException;

public class PortalVerticle extends AbstractVerticle {

  private static final int _PORT = 8081;

  private static final Logger logger = LoggerFactory.getLogger(PortalVerticle.class);

  @Override
  public void start() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {


    Router router = Router.router(vertx);

    // Serving static resources
    router.route("/scripts/*").handler(StaticHandler.create("static/scripts").setCachingEnabled(false).setIncludeHidden(true).setDirectoryListing(true));
    router.route("/assets/*").handler(StaticHandler.create("static/assets").setCachingEnabled(false).setIncludeHidden(true).setDirectoryListing(true));
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
