#!/usr/bin/env bash

export LAUNCHER="io.vertx.core.Launcher"
export VERTICLE="com.shenmao.chuhe.MainVerticle"
export CMD="mvn compile"
export VERTX_CMD="run"

mvn clean compile

rm -f target/classes/static/images/product
ln -s ../../../../file_uploads/images/product target/classes/static/images/product

mvn dependency:copy-dependencies
java \
  -cp  $(echo target/dependency/*.jar | tr ' ' ':'):"target/classes" \
  $LAUNCHER $VERTX_CMD $VERTICLE \
  --redeploy="src/main/**/*" --on-redeploy="$CMD" \
  --launcher-class=$LAUNCHER \
  $@
