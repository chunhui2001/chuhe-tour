package com.shenmao.chuhe.database.chuhe;

import com.google.common.base.Strings;
import com.shenmao.chuhe.database.chuhe.sqlqueries.ChuheSqlQuery;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.sql.SQLClient;
import io.vertx.rx.java.RxHelper;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

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

        if (!product.containsKey("product_spec") || product.getString("product_spec") == null
                || product.getString("product_spec").trim().equals("")) {
            product.put("product_spec", "无");
        }

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

    @Override
    public ChuheDbService createOrder(JsonObject order, Handler<AsyncResult<Long>> resultHandler) {

        String createOrderSql = sqlQueries.get(ChuheSqlQuery.CREATE_ORDER);
        LOGGER.info( createOrderSql);

        JsonArray data = new JsonArray()
                .add(order.getValue("order_flow_no"));

        data.add(order.getValue("order_type"));
        data.add(order.getValue("order_money"));

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
                resultHandler.handle(Future.succeededFuture(reply.result().getKeys().getLong(0)));
            } else {
                resultHandler.handle(Future.failedFuture(reply.cause()));
            }
        });


        return this;
    }

}
