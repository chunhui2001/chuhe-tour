package com.shenmao.chuhe.database.chuhe;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.rxjava.ext.jdbc.JDBCClient;

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
}
