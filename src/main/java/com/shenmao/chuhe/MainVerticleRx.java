package com.shenmao.chuhe;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Single;

public class MainVerticleRx extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);


  private Single<String> deployWikiPageVerticle() {
    return vertx.rxDeployVerticle(
      "com.shenmao.chuhe.verticle.WikiPageVerticle"
    , new DeploymentOptions().setInstances(1));
  }

  private Single<String> startHttpServer() {
    return vertx.rxDeployVerticle(
      "com.shenmao.chuhe.verticle.PortalVerticle",
      new DeploymentOptions().setInstances(3));
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {


    deployWikiPageVerticle()
      .flatMap(id -> {
        return startHttpServer();
      }).subscribe( id -> startFuture.complete(), startFuture::fail);

  }





}
