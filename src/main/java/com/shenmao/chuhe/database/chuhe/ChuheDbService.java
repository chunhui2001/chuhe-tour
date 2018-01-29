package com.shenmao.chuhe.database.chuhe;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.sql.SQLClient;

import java.util.HashMap;
import java.util.List;

@ProxyGen
public interface ChuheDbService {

    public static ChuheDbService create(SQLClient dbClient, HashMap<ChuheSqlQuery, String> sqlQueries,
                                        Handler<AsyncResult<ChuheDbService>> readyHandler) {
        return new ChuheDbServiceImpl(dbClient, sqlQueries, readyHandler);
    }

    public static ChuheDbService createProxy(Vertx vertx, String address) {
        return new ChuheDbServiceVertxEBProxy(vertx, address);
    }

    @Fluent
    ChuheDbService fetchAllProducts(Handler<AsyncResult<List<JsonObject>>> resultHandler);

    @Fluent
    ChuheDbService createProducts(JsonObject product, Handler<AsyncResult<Long>> resultHandler);

    @Fluent
    ChuheDbService fetchProductById(Long productId, Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    ChuheDbService deleteProductById(Long productId, Handler<AsyncResult<Boolean>> resultHandler);

}
