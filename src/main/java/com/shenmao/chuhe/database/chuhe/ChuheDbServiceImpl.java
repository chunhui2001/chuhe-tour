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
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.rxjava.ext.sql.SQLClient;
import io.vertx.rx.java.RxHelper;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;
import rx.exceptions.CompositeException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

        this.createTable(sqlQueries.get(ChuheSqlQuery.CREATE_ORDER_ITEMS_SALES_TABLE), voidAsyncResult -> {
            readyHandler.handle(Future.succeededFuture(this));
        });

        this.createTable(sqlQueries.get(ChuheSqlQuery.CREATE_STOCK_TABLE), voidAsyncResult -> {
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


    @Fluent
    public ChuheDbService fetchProductsByIdList(List<Long> productIdList, Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        Single<ResultSet> productListSingle = this.dbClient.rxQuery("");

        return this;
    }


    public Single<ResultSet> fetchProductsByIdList(List<Long> productIdList) {

        StringBuilder sql = new StringBuilder(sqlQueries.get(ChuheSqlQuery.GET_PRODUCTS_BY_IDLIST));

        JsonArray params = replaceAndGetSqlParams(sql, "_product_id_list_", productIdList);

        LOGGER.info(sql.toString());

        Single<ResultSet> productListSingle = this.dbClient.rxQueryWithParams(sql.toString(), params);

        return productListSingle;

    }



    final SimpleDateFormat _DATE_FM_T = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    final SimpleDateFormat _DATE_FM_MINUTE = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
    final SimpleDateFormat _DATE_FM_HOUR = new SimpleDateFormat("yyyy年MM月dd日 HH时");

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
            product.put("created_at", _DATE_FM_MINUTE.format(create_at));
        }

        if (last_updated != null) {
            product.put("last_updated", _DATE_FM_MINUTE.format(last_updated));
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

    private JsonArray replaceAndGetSqlParams(StringBuilder sql, String s, List<Long> productIdList) {

        JsonArray sqlParam = new JsonArray();
        StringJoiner joiner = new StringJoiner(",");

        productIdList.forEach(pid -> {
            sqlParam.add(pid);
            joiner.add( "?");
        });

        String temp = sql.toString().replaceAll(s, joiner.toString());

        sql.delete(0, sql.toString().length()).append(temp);

        return sqlParam;

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

    private Double getOrderMoney(List<JsonObject> orderDetailItemList, String orderType) {

        Double orderMonty = 0.0;

        for (int i=0; i<orderDetailItemList.size(); i++) {

            Double price = orderDetailItemList.get(i).getDouble("product_price");
            Double count = 0.0;

            if ("sales".equals(orderType))
                count = orderDetailItemList.get(i).getDouble("product_sale_count");

            if ("replenish".equals(orderType))
                count = orderDetailItemList.get(i).getDouble("product_buy_count");

            orderMonty += price * count;
        }

        return orderMonty;
    }

    private ChuheDbService createOrderDeatailReplenish(Long orderId, List<JsonObject> orderDetailItemList, Handler<AsyncResult<Long>> resultHandler) {



        return this;
    }

    private List<Long> getProductIdInOrderItems(List<JsonObject> orderDetailItemList) {

        return orderDetailItemList
                .stream()
                .map(item -> item.getLong("product_id"))
                .collect(Collectors.toList());

    }

    @Override
    public ChuheDbService createOrder(JsonObject order, List<JsonObject> orderDetailItemList, Handler<AsyncResult<Long>> resultHandler) {

        String createOrderSql = sqlQueries.get(ChuheSqlQuery.CREATE_ORDER);
        LOGGER.info( createOrderSql);

        JsonArray data = new JsonArray()
                .add(order.getValue("order_flow_no"));

        data.add(order.getValue("order_type"));
        data.add(getOrderMoney(orderDetailItemList, order.getString("order_type")));

        if (!order.containsKey("order_date")) {
            order.put("order_date", getDateTimeString());
        }

        data.add(order.getValue("order_date"));

        if (Strings.emptyToNull(order.getString("order_person")) != null) {
            data.add(order.getString("order_person"));
        } else {
            data.addNull();
        }

        if (Strings.emptyToNull(order.getString("user_identity")) != null) {
            data.add(order.getString("user_identity"));
        } else {
            data.addNull();
        }

        if (Strings.emptyToNull(order.getString("order_desc")) != null) {
            data.add(order.getString("order_desc"));
        } else {
            data.addNull();
        }

        // https://github.com/vert-x3/vertx-examples/blob/master/rxjava-1-examples/src/main/java/io/vertx/example/rxjava/database/jdbc/Transaction.java
        this.dbClient.rxGetConnection()
                .flatMap(conn ->
                    conn
                        .rxSetAutoCommit(false)
                        // insert order
                        .flatMap(autoCommit -> conn.rxUpdateWithParams(createOrderSql, data))
                        // get products
                        .flatMap(updateResult -> {
                            Long newOrderId = updateResult.getKeys().getLong(0);
                            order.put("order_id", newOrderId);
                            return this.fetchProductsByIdList(getProductIdInOrderItems(orderDetailItemList));
                        })
                        .map(ResultSet::getRows)
                        // inert order items
                        .flatMap(productList -> {

                            order.put("productList", productList);


                            if ("sales".equals(order.getString("order_type"))) {
                                return saveOrderItemsSales(order, productList, orderDetailItemList);
                            }

                            return saveOrderItemsReplenish(order, productList, orderDetailItemList);

                        })
                        // save stocks
                        .flatMap(updateResult -> {

                            JsonArray productArray = (JsonArray)order.getValue("productList");
                            List<JsonObject> productList = new ArrayList<>();
                            // List<JsonObject> productList = (List<JsonObject>)order.getValue("productList");

                            for (int i=0; i<productArray.size(); i++) {
                                productList.add(productArray.getJsonObject(i));
                            }

                            return saveStocks(order, productList, orderDetailItemList);
                        })
                        .flatMap(updateResult -> conn.rxCommit().map(commit -> updateResult))
                        .onErrorResumeNext(ex ->
                                conn.rxRollback()
                                    .onErrorResumeNext(ex2 ->
                                            Single.error(new CompositeException(ex, ex2))
                                    ).flatMap(ignore -> Single.error(ex))
                        ).doAfterTerminate(conn::close)
                ).subscribe(updateResult -> {
                    resultHandler.handle(Future.succeededFuture(order.getLong("order_id")));
                }, error -> {

                    resultHandler.handle(Future.failedFuture(error.getMessage()));
                });

        return this;
    }

    private List<JsonObject> processOrderItems(List<JsonObject> orderItems, String orderType) {

        for (int i=0; i<orderItems.size(); i++) {

            orderItems.get(i).put("order_item_no", i + 1);

            Double product_price = orderItems.get(i).getDouble("product_price");
            Double product_buy_count = 0.0;

            if ("replenish".equals(orderType))
                product_buy_count = orderItems.get(i).getDouble("product_buy_count");

            if ("sales".equals(orderType))
                product_buy_count = orderItems.get(i).getDouble("product_sale_count");

            BigDecimal bg = new BigDecimal(product_price * product_buy_count).setScale(2, RoundingMode.UP);
            orderItems.get(i).put("item_total", bg.doubleValue());

            if (orderItems.get(i).getString("order_item_desc") == null
                    || orderItems.get(i).getString("order_item_desc").trim().isEmpty()) {
                orderItems.get(i).put("order_item_desc", "无");
            }

            if (orderItems.get(i).getString("product_spec") == null
                    || orderItems.get(i).getString("product_spec").trim().isEmpty()) {
                orderItems.get(i).put("product_spec", "无");
            }

        }

        return orderItems;
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
            order.put("order_date", _DATE_FM_MINUTE.format(order_date));
        }

        if (create_at != null) {
            order.put("created_at", _DATE_FM_MINUTE.format(create_at));
        }

        if (last_updated != null) {
            order.put("last_updated", _DATE_FM_MINUTE.format(last_updated));
        }


        return order;

    }

    @Override
    public ChuheDbService fetchAllOrders(String orderType, Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        String allOrdersSql = sqlQueries.get(ChuheSqlQuery.ALL_ORDERS);
        LOGGER.info( allOrdersSql);

        JsonArray params = new JsonArray();

        params.add(orderType);

        this.dbClient.rxQueryWithParams(allOrdersSql, params)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(order -> this.processOrder(order))
                // .sorted()
                .collect(ArrayList<JsonObject>::new, List::add)
                .subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }

    private String getProductNameById(Long productId,  List<JsonObject> productList) {

        JsonObject result = productList.stream().filter(p -> {
            return p.getLong("product_id").compareTo(productId) == 0;
        }).findFirst().get();

        return result != null ? result.getString("product_name") : null;
    }

    public Single<UpdateResult> saveOrderItemsReplenish(JsonObject order, List<JsonObject> productList, List<JsonObject> orderDetailItemList) {

        String allOrdersReplenishSql = sqlQueries.get(ChuheSqlQuery.SAVE_ORDER_ITEMS_REPLENISH);

        String s = allOrdersReplenishSql.substring(allOrdersReplenishSql.lastIndexOf("("));     // (?, ?, ?, ?, ?, ?, ?)

        StringJoiner joiner = new StringJoiner(",");
        for (int i=0; i<orderDetailItemList.size() - 1; i++) joiner.add(s);
        if (orderDetailItemList.size() > 1) allOrdersReplenishSql = allOrdersReplenishSql + "," + joiner.toString();

        LOGGER.info( allOrdersReplenishSql);

        JsonArray params = new JsonArray();

        for (int i=0; i<orderDetailItemList.size(); i++) {

            JsonObject item = orderDetailItemList.get(i);

            String productName = getProductNameById(item.getLong("product_id"), productList);

            params.add(order.getValue("order_type"));
            params.add(order.getValue("order_id"));
            params.add(item.getValue("product_id"));

            if (productName != null) params.add(productName);
            else params.addNull();

            params.add(item.getValue("product_price"));
            params.add(item.getValue("product_buy_count"));

            if (!item.containsKey("order_item_desc") || item.getString("order_item_desc").trim().length() == 0) {
                params.addNull();
            } else {
                params.add(item.getValue("order_item_desc"));
            }
        }

        Single<UpdateResult> saveOrderItemsSingle = this.dbClient.rxUpdateWithParams(allOrdersReplenishSql, params);

        return saveOrderItemsSingle;

    }


    public Single<UpdateResult> saveStocks(JsonObject order, List<JsonObject> productList, List<JsonObject> orderDetailItemList) {

        String saveStocksSql = sqlQueries.get(ChuheSqlQuery.SAVE_STOCK);
        String s = saveStocksSql.substring(saveStocksSql.lastIndexOf("("));     // (?, ?, ?, ?, ?, ?, ?)

        StringJoiner joiner = new StringJoiner(",");
        for (int i=0; i<orderDetailItemList.size() - 1; i++) joiner.add(s);
        if (orderDetailItemList.size() > 1) saveStocksSql = saveStocksSql + "," + joiner.toString();

        LOGGER.info(saveStocksSql);

        JsonArray params = new JsonArray();

        for (int i=0; i<orderDetailItemList.size(); i++) {

            JsonObject item = orderDetailItemList.get(i);

            String productName = getProductNameById(item.getLong("product_id"), productList);

            params.add(order.getValue("order_id"));
            params.add(order.getValue("order_type"));
            params.add(item.getValue("product_id"));

            if (productName != null) params.add(productName);
            else params.addNull();

            params.add(order.getValue("order_date"));

            Double count = 0.0;

            if (order.getString("order_type").equals("replenish"))
                count = item.getDouble("product_buy_count");
            else if (order.getString("order_type").equals("sales"))
                count = item.getDouble("product_sale_count");

            params.add(count);
            params.add(count * item.getDouble("product_price"));

            params.add(0.0);
            params.add(0);

        }

        Single<UpdateResult> saveStockSingle = this.dbClient.rxUpdateWithParams(saveStocksSql, params);

        return saveStockSingle;

    }


    public Single<UpdateResult> saveOrderItemsSales(JsonObject order, List<JsonObject> productList, List<JsonObject> orderDetailItemList) {

        String allOrdersSalesSql = sqlQueries.get(ChuheSqlQuery.SAVE_ORDER_ITEMS_SALES);

        String s = allOrdersSalesSql.substring(allOrdersSalesSql.lastIndexOf("("));     // (?, ?, ?, ?, ?, ?, ?)

        StringJoiner joiner = new StringJoiner(",");
        for (int i=0; i<orderDetailItemList.size() - 1; i++) joiner.add(s);
        if (orderDetailItemList.size() > 1) allOrdersSalesSql = allOrdersSalesSql + "," + joiner.toString();

        LOGGER.info( allOrdersSalesSql);

        JsonArray params = new JsonArray();

        for (int i=0; i<orderDetailItemList.size(); i++) {

            JsonObject item = orderDetailItemList.get(i);

            String productName = getProductNameById(item.getLong("product_id"), productList);

            params.add(order.getValue("order_type"));
            params.add(order.getValue("order_id"));
            params.add(item.getValue("product_id"));

            if (productName != null) params.add(productName);
            else params.addNull();

            params.add(item.getValue("product_price"));
            params.add(item.getValue("product_sale_count"));

            if (!item.containsKey("order_item_desc") || item.getString("order_item_desc").trim().length() == 0) {
                params.addNull();
            } else {
                params.add(item.getValue("order_item_desc"));
            }
        }

        Single<UpdateResult> saveOrderItemsSingle = this.dbClient.rxUpdateWithParams(allOrdersSalesSql, params);

        return saveOrderItemsSingle;

    }

    @Override
    public ChuheDbService getOrderDetail(Long orderId, String orderType, Handler<AsyncResult<JsonObject>> resultHandler) {

        String getOrderSql = sqlQueries.get(ChuheSqlQuery.GET_ORDER);
        String getOrderItemsReplenishSql = sqlQueries.get(ChuheSqlQuery.GET_ORDER_ITEMS_REPLENISH);
        String getOrderItemsSalesSql = sqlQueries.get(ChuheSqlQuery.GET_ORDER_ITEMS_SALES);

        LOGGER.info(getOrderSql);

        JsonArray parsms = new JsonArray().add(orderId);

        JsonArray returnOrder = new JsonArray();

        this.dbClient.rxQueryWithParams(getOrderSql, parsms)
                .map(resultSet -> {
                    if (resultSet.getRows().size() == 0) return new JsonObject();
                    JsonObject targetOrder = resultSet.getRows().get(0);
                    return processOrder(targetOrder);
                })
                .flatMap(targetOrder -> {

                    returnOrder.add(targetOrder);

                    String getOrderItemsSql = null;

                    if (targetOrder.fieldNames().size() == 0) {
                        getOrderItemsSql = getOrderItemsReplenishSql;
                    }

                    if ("replenish".equals(targetOrder.getString("order_type"))) {
                        getOrderItemsSql = getOrderItemsReplenishSql;
                    }

                    if ("sales".equals(targetOrder.getString("order_type"))) {
                        getOrderItemsSql = getOrderItemsSalesSql;
                    }


                    LOGGER.info(getOrderItemsSql);

                    return this.dbClient.rxQueryWithParams(getOrderItemsSql, parsms);
                })
                .map(orderItems -> {
                    System.out.println(1);
                    if (orderItems.getRows().size() == 0) return new ArrayList<JsonObject>();
                    System.out.println(2);
                    return processOrderItems(orderItems.getRows(), orderType);
                })
                .subscribe(orderIems -> {
                    System.out.println(3);
                    if (orderIems.size() == 0) {
                        System.out.println(4);
                        resultHandler.handle(Future.succeededFuture(new JsonObject()));
                    } else {
                        System.out.println(5);
                        returnOrder.getJsonObject(0).put("order_items", orderIems);
                        resultHandler.handle(Future.succeededFuture(returnOrder.getJsonObject(0)));
                    }
                }, error -> {
                    System.out.println(6);
                        resultHandler.handle(Future.failedFuture(error.getMessage()));
                });

        return this;

    }


    @Override
    public ChuheDbService fetchAllStocks(Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        String fetchAllStocksSql = this.sqlQueries.get(ChuheSqlQuery.GET_ALL_STOCK);

        LOGGER.info(fetchAllStocksSql);

        this.dbClient.rxQuery(fetchAllStocksSql)
                .flatMapObservable(resultSet -> Observable.from(resultSet.getRows()))
                .map(stock -> {

                    Date orderDate = null;

                    if (stock.containsKey("order_date")) {
                        try {
                            orderDate = _DATE_FM_T.parse(stock.getString("order_date"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    if (orderDate != null) {
                        stock.put("order_date", _DATE_FM_HOUR.format(orderDate));
                    } else {
                        stock.put("order_date", "无");
                    }

                    switch (stock.getString("order_type")) {
                        case "replenish":
                            stock.put("order_type_name", "进货");
                            break;
                        case "sales":
                            stock.put("order_type_name", "销售");
                            break;
                        default:
                            stock.put("order_type_name", stock.getString("order_type"));
                    }


                    return stock;
                })
                .collect(ArrayList<JsonObject>::new, List::add)
                .subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }

}
