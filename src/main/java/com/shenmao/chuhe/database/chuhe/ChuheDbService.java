package com.shenmao.chuhe.database.chuhe;

import com.shenmao.chuhe.database.chuhe.sqlqueries.ChuheSqlQuery;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.rxjava.ext.sql.SQLClient;
import rx.Single;

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
    ChuheDbService filterProductsByName(String pName, Handler<AsyncResult<List<JsonObject>>> resultHandler);

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
    ChuheDbService fetchAllStocks(Long productId, Handler<AsyncResult<List<JsonObject>>> resultHandler);

    // users
    @Fluent
    ChuheDbService fetchAllUsers(Handler<AsyncResult<List<JsonObject>>> resultHandler);

    @Fluent
    ChuheDbService createCustomer(JsonObject user, JsonArray userRoles, Handler<AsyncResult<Long>> resultHandler) ;

    @Fluent
    ChuheDbService createUserBeforeCheck(JsonObject user, String check_code, String check_code_sign, String validate_code_sign, Handler<AsyncResult<Boolean>> resultHandler) ;


    @Fluent
    ChuheDbService createUser(JsonObject user, String check_code, String check_code_sign, String validate_code_sign, JsonArray userRoles, Handler<AsyncResult<Long>> resultHandler) ;

    @Fluent
    ChuheDbService deleteUserBatch(List<Long> userIdList, Handler<AsyncResult<Integer>> resultHandler);

    @Fluent
    ChuheDbService userDetail(Long userId, Handler<AsyncResult<JsonObject>> resultHandler) ;

    @Fluent
    ChuheDbService updateUser(Long userId, JsonObject newUser, Handler<AsyncResult<Integer>> resultHandler);

    @Fluent
    ChuheDbService updateUserRoles(Long userId, String userRoles, Handler<AsyncResult<Integer>> resultHandler);

    @Fluent
    ChuheDbService validateUser(String username, String passwd, Handler<AsyncResult<Boolean>> resultHandler) ;

    @Fluent
    ChuheDbService userNameDuplicate(String username, Handler<AsyncResult<Boolean>> resultHandler) ;

    // dealers
    @Fluent
    ChuheDbService createDealer(JsonObject dealer, JsonArray userRoles, Handler<AsyncResult<Long>> resultHandler) ;

    @Fluent
    ChuheDbService dealerDetail(Long dealerId, Handler<AsyncResult<JsonObject>> resultHandler) ;


    @Fluent
    ChuheDbService fetchAllDealers(Handler<AsyncResult<List<JsonObject>>> resultHandler) ;

    @Fluent
    ChuheDbService deleteDealerBatch(List<Long> dealerIdList, Handler<AsyncResult<Integer>> resultHandler);

    @Fluent
    ChuheDbService updateDealer(Long dealerId, JsonObject dealer, Handler<AsyncResult<Integer>> resultHandler);

    // check_code
    @Fluent
    ChuheDbService createCheckCode(JsonObject checkcode, Integer seconds, Handler<AsyncResult<Long>> resultHandler) ;

    @Fluent
    ChuheDbService validateCheckCode(String sign, String code, String receiver, Handler<AsyncResult<String>> resultHandler) ;

    @Fluent
    ChuheDbService validateCheckCodeImage(String sign, String code, String receiver, Integer expiredSeconds, String checktype, String client_ip, String client_agent, Handler<AsyncResult<JsonObject>> resultHandler) ;

    @Fluent
    ChuheDbService validateCheckCodePhoneOrEmail(String sign, String code, String receiver, String checktype, String client_ip, String client_agent, Handler<AsyncResult<JsonObject>> resultHandler) ;

    @Fluent
    ChuheDbService updateCheckCodeConfirmTime(String sign, String receiver, String channel, Handler<AsyncResult<Long>> resultHandler);

}
