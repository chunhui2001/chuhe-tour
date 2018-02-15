package com.shenmao.chuhe.database.chuhe;

import com.shenmao.chuhe.database.chuhe.sqlqueries.ChuheSqlQuery;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
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

    // products
    @Fluent
    ChuheDbService fetchAllProducts(Handler<AsyncResult<List<JsonObject>>> resultHandler);

    @Fluent
    ChuheDbService createProducts(JsonObject product, Handler<AsyncResult<Long>> resultHandler);

    @Fluent
    ChuheDbService updateProduct(Long productId, JsonObject product, Handler<AsyncResult<Integer>> resultHandler);

    @Fluent
    ChuheDbService fetchProductById(Long productId, Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    ChuheDbService fetchProductsByIdList(List<Long> productIdList, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    @Fluent
    ChuheDbService deleteProductById(Long productId, Handler<AsyncResult<Integer>> resultHandler);

    @Fluent
    ChuheDbService deleteProductBatch(List<Long> productIdList, Handler<AsyncResult<Integer>> resultHandler);


    // orders
    @Fluent
    ChuheDbService createOrder(JsonObject order, List<JsonObject> orderDetailItemList, Handler<AsyncResult<Long>> resultHandler);

    @Fluent
    ChuheDbService fetchAllOrders(String orderType, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    @Fluent
    ChuheDbService getOrderDetail(Long orderId, String orderType, Handler<AsyncResult<JsonObject>> resultHandler);


    // stock
    @Fluent
    ChuheDbService fetchAllStocks(Handler<AsyncResult<List<JsonObject>>> resultHandler);


    // users
    @Fluent
    ChuheDbService createUser(JsonObject user, Handler<AsyncResult<Long>> resultHandler) ;

    // dealers
    @Fluent
    ChuheDbService createDealer(JsonObject dealer, Handler<AsyncResult<Long>> resultHandler) ;

    @Fluent
    ChuheDbService dealerDetail(Long dealerId, Handler<AsyncResult<JsonObject>> resultHandler) ;


    @Fluent
    ChuheDbService fetchAllDealers(Handler<AsyncResult<List<JsonObject>>> resultHandler) ;

    @Fluent
    ChuheDbService deleteDealerBatch(List<Long> dealerIdList, Handler<AsyncResult<Integer>> resultHandler);


    @Fluent
    ChuheDbService updateDealer(Long dealerId, JsonObject dealer, Handler<AsyncResult<Integer>> resultHandler);

}
