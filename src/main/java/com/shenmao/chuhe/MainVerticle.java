package com.shenmao.chuhe;

import com.shenmao.chuhe.verticle.ChuheDbVerticle;
import com.shenmao.chuhe.verticle.PortalVerticle;
import com.shenmao.chuhe.verticle.WikiPageVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);


  private Future<String> deployWikiPageVerticle() {
    Future<String> future = Future.future();
    vertx.deployVerticle(new WikiPageVerticle(), future.completer());
    return future;
  }

  private Future<String> deployChuheDbVerticle() {
    Future<String> future = Future.future();
    vertx.deployVerticle(new ChuheDbVerticle(), future.completer());
    return future;
  }

  private Future<String> startHttpServer() {

    Future<String> future = Future.future();
    vertx.deployVerticle(
            "com.shenmao.chuhe.verticle.PortalVerticle"
      , future.completer());
    return future;
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Future<String> steps = deployWikiPageVerticle()
                           .compose(id -> deployChuheDbVerticle())
                           .compose(id -> startHttpServer());

    steps.setHandler(ar -> {

        if (ar.succeeded()) {
          startFuture.complete();
        } else {
          startFuture.fail(ar.cause());
        }
    });

  }





}
