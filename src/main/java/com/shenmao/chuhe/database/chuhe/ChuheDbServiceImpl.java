package com.shenmao.chuhe.database.chuhe;

import com.google.common.base.Strings;
import com.shenmao.chuhe.database.chuhe.sqlqueries.ChuheSqlQuery;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.sql.SQLClient;
import io.vertx.rx.java.RxHelper;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChuheDbServiceImpl implements ChuheDbService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ChuheDbServiceImpl.class);
    private final HashMap<ChuheSqlQuery, String> sqlQueries;
    private final SQLClient dbClient;

    ChuheDbServiceImpl(SQLClient dbClient, HashMap<ChuheSqlQuery, String> sqlQueries,
                       Handler<AsyncResult<ChuheDbService>> readyHandler) {

        this.dbClient = dbClient;
        this.sqlQueries = sqlQueries;

        this.createTable(sqlQueries.get(ChuheSqlQuery.CREATE_PRODUCTS_TABLE), voidAsyncResult -> {
            readyHandler.handle(Future.succeededFuture(this));
        });

        this.createTable(sqlQueries.get(ChuheSqlQuery.CREATE_ORDERS_TABLE), voidAsyncResult -> {
            readyHandler.handle(Future.succeededFuture(this));
        });

        this.createTable(sqlQueries.get(ChuheSqlQuery.CREATE_ORDER_ITEMS_REPLENISH_TABLE), voidAsyncResult -> {
            readyHandler.handle(Future.succeededFuture(this));
        });

    }


    public void createTable(String create_sql, Handler<AsyncResult<Void>> resultHandler) {

        dbClient.getConnection(ar -> {
            if (ar.failed()) {
                LOGGER.error("Could not open Chuhe database connection", ar.cause());
                resultHandler.handle(Future.failedFuture(ar.cause()));
            } else {
                SQLConnection connection = ar.result().getDelegate();

                LOGGER.error(create_sql);

                connection.execute(create_sql, create -> {
                    connection.close();
                    if (create.succeeded())
                        resultHandler.handle(Future.succeededFuture());
                    else {
                        LOGGER.error("Chuhe Database preparation error", Future.failedFuture(create.cause()));
                        resultHandler.handle(Future.failedFuture(create.cause()));
                    }
                });
            }
        });
    }


    @Fluent
    public ChuheDbService fetchProductById(Long productId, Handler<AsyncResult<JsonObject>> resultHandler) {

        String fetchProductByIdSql = sqlQueries.get(ChuheSqlQuery.GET_PRODUCT);

        LOGGER.info( fetchProductByIdSql);

        JsonArray sqlParams = new JsonArray().add(productId);

        Single<JsonObject> result = this.dbClient.rxQueryWithParams(fetchProductByIdSql, sqlParams)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(product -> this.processProduct(product))
                .defaultIfEmpty(new JsonObject()).toSingle();

        result.subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }

    final SimpleDateFormat _DATE_FM_T = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    final SimpleDateFormat _DATE_FM_HOUR = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");

    private JsonObject processProduct(JsonObject product) {

        Date create_at = null;
        Date last_updated = null;

        try {
            create_at = _DATE_FM_T.parse(product.getString("created_at"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            last_updated = _DATE_FM_T.parse(product.getString("last_updated"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (create_at != null) {
            product.put("created_at", _DATE_FM_HOUR.format(create_at));
        }

        if (last_updated != null) {
            product.put("last_updated", _DATE_FM_HOUR.format(last_updated));
        }

        byte[] productDesc = product.getBinary("product_desc");


        if (!product.containsKey("product_desc")
                || productDesc == null || (new String(productDesc)).trim().isEmpty()) {
            product.put("product_desc", "无");
        } else {
            product.put("product_desc", new String(productDesc));
        }

        return product;

    }

    @Override
    public ChuheDbService fetchAllProducts(Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        String allProductSql = sqlQueries.get(ChuheSqlQuery.ALL_PRODUCTS);
        LOGGER.info( allProductSql);


        this.dbClient.rxQuery(allProductSql)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(product -> this.processProduct(product))
                // .sorted()
                .collect(ArrayList<JsonObject>::new, List::add)
                .subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }


    @Override
    public ChuheDbService createProducts(JsonObject product, Handler<AsyncResult<Long>> resultHandler) {

        String createProductSql = sqlQueries.get(ChuheSqlQuery.CREATE_PRODUCT);
        LOGGER.info( createProductSql);

        JsonArray data = new JsonArray()
                .add(product.getValue("product_name"))
                .add(product.getValue("product_unit"))
                .add(product.getValue("product_price"));

        if (Strings.emptyToNull(product.getString("product_spec")) != null) {
            data.add(product.getString("product_spec"));
        } else {
            data.addNull();
        }

        if (Strings.emptyToNull(product.getString("product_desc")) != null) {
            data.add(product.getString("product_desc"));
        } else {
            data.addNull();
        }


        this.dbClient.updateWithParams(createProductSql, data, reply -> {

            if (reply.succeeded()) {
                resultHandler.handle(Future.succeededFuture(reply.result().getKeys().getLong(0)));
            } else {
                resultHandler.handle(Future.failedFuture(reply.cause()));
            }
        });


        return this;
    }


    @Override
    public ChuheDbService updateProduct(Long productId, JsonObject newProduct, Handler<AsyncResult<Integer>> resultHandler) {

        this.fetchProductById(productId, ar -> {

            if (!ar.succeeded()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            JsonObject product;
            JsonObject oldProduct = ar.result();

            if (oldProduct.fieldNames().size() == 0) {
                resultHandler.handle(Future.succeededFuture(0));
                return;
            }

            System.out.println(oldProduct.encode() + ", oldProduct");

            newProduct.fieldNames().forEach(f -> {
                oldProduct.put(f, newProduct.getValue(f));
            });

            product = oldProduct;

            String updateProductSql = sqlQueries.get(ChuheSqlQuery.SAVE_PRODUCT);
            LOGGER.info(updateProductSql);

            JsonArray sqlParams = new JsonArray()
                    .add(product.getValue("product_name"))
                    .add(product.getValue("product_unit"))
                    .add(product.getValue("product_price"));

            String productSpecStr = Strings.emptyToNull(product.getString("product_spec"));
            String productDescStr = Strings.emptyToNull(product.getString("product_desc"));

            if (productSpecStr != null && !productSpecStr.trim().equals("无")) {
                sqlParams.add(product.getString("product_spec"));
            } else {
                sqlParams.addNull();
            }

            if ( productDescStr != null && !productDescStr.trim().equals("无")) {
                sqlParams.add(product.getString("product_desc"));
            } else {
                sqlParams.addNull();
            }

            sqlParams.add(productId);

            this.dbClient.updateWithParams(updateProductSql, sqlParams, reply -> {
                if (reply.succeeded()) {
                    resultHandler.handle(Future.succeededFuture(reply.result().getUpdated()));
                } else {
                    resultHandler.handle(Future.failedFuture(reply.cause()));
                }
            });


        });



        return this;
    }



    @Override
    public ChuheDbService deleteProductById(Long productId, Handler<AsyncResult<Integer>> resultHandler) {

        String deleteProductSql = sqlQueries.get(ChuheSqlQuery.DELETE_PRODUCT);
        LOGGER.info(deleteProductSql);

        JsonArray sqlParam = new JsonArray().add(productId);

        this.dbClient.updateWithParams(deleteProductSql, sqlParam, reply -> {

            if (reply.succeeded()) {
                resultHandler.handle(Future.succeededFuture(reply.result().getUpdated()));
            } else {
                resultHandler.handle(Future.failedFuture(reply.cause()));
            }
        });

        return this;
    }

    @Override
    public ChuheDbService deleteProductBatch(List<Long> productIdList, Handler<AsyncResult<Integer>> resultHandler) {

        String deleteProductSqlBatch = sqlQueries.get(ChuheSqlQuery.DELETE_PRODUCT_BATCH);

        JsonArray sqlParam = new JsonArray();
        StringJoiner joiner = new StringJoiner(",");

        productIdList.forEach(pid -> {
            sqlParam.add(pid);
            joiner.add( "?");
        });

        deleteProductSqlBatch = deleteProductSqlBatch.replaceAll("_product_id_list_", joiner.toString());

        LOGGER.info(deleteProductSqlBatch);

        this.dbClient.updateWithParams(deleteProductSqlBatch, sqlParam, reply -> {

            if (reply.succeeded()) {
                resultHandler.handle(Future.succeededFuture(reply.result().getUpdated()));
            } else {
                resultHandler.handle(Future.failedFuture(reply.cause()));
            }
        });

        return this;
    }

    private String getDateTimeString() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sf.format(new Date());
    }

    private Double getOrderMoney(List<JsonObject> orderDetailItemList) {

        Double orderMonty = 0.0;

        for (int i=0; i<orderDetailItemList.size(); i++) {
            Double price = orderDetailItemList.get(i).getDouble("product_price");
            Double count = orderDetailItemList.get(i).getDouble("product_buy_count");
            orderMonty += price * count;
        }

        return orderMonty;
    }

    private ChuheDbService createOrderDeatailReplenish(Long orderId, List<JsonObject> orderDetailItemList, Handler<AsyncResult<Long>> resultHandler) {



        return this;
    }

    @Override
    public ChuheDbService createOrder(JsonObject order, List<JsonObject> orderDetailItemList, Handler<AsyncResult<Long>> resultHandler) {

        String createOrderSql = sqlQueries.get(ChuheSqlQuery.CREATE_ORDER);
        LOGGER.info( createOrderSql);


        JsonArray data = new JsonArray()
                .add(order.getValue("order_flow_no"));

        data.add(order.getValue("order_type"));
        data.add(getOrderMoney(orderDetailItemList));

        if (order.containsKey("order_date")) {
            data.add(order.getValue("order_date") + " 00:00:00.000");
        } else {
            data.add(getDateTimeString());
        }

        if (Strings.emptyToNull(order.getString("order_person")) != null) {
            data.add(order.getString("order_person"));
        } else {
            data.addNull();
        }

        data.add(order.getString("user_identity"));

        if (Strings.emptyToNull(order.getString("order_desc")) != null) {
            data.add(order.getString("order_desc"));
        } else {
            data.addNull();
        }


        this.dbClient.updateWithParams(createOrderSql, data, reply -> {

            if (reply.succeeded()) {
                Long newOrderId = reply.result().getKeys().getLong(0);
                order.put("order_id", newOrderId);
                saveOrderItemsReplenish(order, orderDetailItemList, ar -> {
                    if (ar.succeeded()) {
                        System.out.println(ar.result() + ", save order detail item list successful");
                    }    else {
                        System.out.println("save order detail item list failes");
                    }
                });

                resultHandler.handle(Future.succeededFuture(newOrderId));
            } else {
                resultHandler.handle(Future.failedFuture(reply.cause()));
            }
        });


        return this;
    }



    private JsonObject processOrder(JsonObject order) {

        Date order_date = null;
        Date create_at = null;
        Date last_updated = null;
        String orderType = null;

        if (!order.containsKey("order_type") || order.getString("order_type") == null
                || order.getString("order_type").trim().equals("")) {
            order.put("order_type", "无");
        } else {
            String orderTypeName = null;
            orderType = order.getString("order_type").trim();

            switch (orderType) {
                case "replenish":
                    orderTypeName = "进货";
                    break;
                case "returned":
                    orderTypeName = "退货";
                    break;
                case "sales":
                    orderTypeName = "销售";
                    break;
                default:
                    orderTypeName = "未知(" + orderType + ")";
            }

            order.put("order_type_name", orderTypeName);
        }

        if (!order.containsKey("order_person") || order.getString("order_person") == null
                || order.getString("order_person").trim().equals("")) {
            order.put("order_person", "无");
        }

        if (!order.containsKey("order_desc") || order.getString("order_desc") == null
                || order.getString("order_desc").trim().equals("")) {
            order.put("order_desc", "无");
        }

        try {
            order_date = _DATE_FM_T.parse(order.getString("order_date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            create_at = _DATE_FM_T.parse(order.getString("created_at"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            last_updated = _DATE_FM_T.parse(order.getString("last_updated"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (order_date != null) {
            order.put("order_date", _DATE_FM_HOUR.format(order_date));
        }

        if (create_at != null) {
            order.put("created_at", _DATE_FM_HOUR.format(create_at));
        }

        if (last_updated != null) {
            order.put("last_updated", _DATE_FM_HOUR.format(last_updated));
        }


        return order;

    }

    @Override
    public ChuheDbService fetchAllOrders(Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        String allOrdersSql = sqlQueries.get(ChuheSqlQuery.ALL_ORDERS);
        LOGGER.info( allOrdersSql);


        this.dbClient.rxQuery(allOrdersSql)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(order -> this.processOrder(order))
                // .sorted()
                .collect(ArrayList<JsonObject>::new, List::add)
                .subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }

    @Override
    public ChuheDbService saveOrderItemsReplenish(JsonObject order, List<JsonObject> orderDetailItemList,
                                                  Handler<AsyncResult<Integer>> resultHandler) {

        String allOrdersReplenishSql = sqlQueries.get(ChuheSqlQuery.SAVE_ORDER_ITEMS_REPLENISH);

        StringJoiner joiner = new StringJoiner(",");
        for (int i=0; i<orderDetailItemList.size() - 1; i++) joiner.add("(?, ?, ?, ?, ?, ?, ?)");
        allOrdersReplenishSql = allOrdersReplenishSql + "," + joiner.toString();

        LOGGER.info( allOrdersReplenishSql);

        JsonArray params = new JsonArray();

        for (int i=0; i<orderDetailItemList.size(); i++) {

            JsonObject item = orderDetailItemList.get(i);

            params.add(order.getValue("order_type"));
            params.add(order.getValue("order_id"));
            params.add(item.getValue("product_id"));
            //params.add(item.getValue("product_name"));
            params.addNull();
            params.add(item.getValue("product_price"));
            params.add(item.getValue("product_buy_count"));

            if (!item.containsKey("order_item_desc") || item.getString("order_item_desc").trim().length() == 0) {
                params.addNull();
            } else {
                params.add(item.getValue("order_item_desc"));
            }
        }


        this.dbClient.updateWithParams(allOrdersReplenishSql, params, resultAsyncResult -> {

            if (resultAsyncResult.succeeded()) {
                resultHandler.handle(Future.succeededFuture(resultAsyncResult.result().getUpdated()));
            } else {
                resultHandler.handle(Future.failedFuture(resultAsyncResult.cause()));
            }
        });



        return this;

    }

}
