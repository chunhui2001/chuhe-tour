package com.shenmao.chuhe.database.chuhe;

import com.google.common.base.Strings;
import com.shenmao.chuhe.commons.validate.MyValidate;
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
import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.stream.Collectors;

import com.shenmao.chuhe.commons.DatetimeHelper;

public class ChuheDbServiceImpl implements ChuheDbService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ChuheDbServiceImpl.class);
    private final HashMap<ChuheSqlQuery, String> sqlQueries;
    private final SQLClient dbClient;

    ChuheDbServiceImpl(SQLClient dbClient, HashMap<ChuheSqlQuery, String> sqlQueries,
                       Handler<AsyncResult<ChuheDbService>> readyHandler) {

        this.dbClient = dbClient;
        this.sqlQueries = sqlQueries;

        this.createTable(sqlQueries.get(ChuheSqlQuery.CREATE_DEALER_TABLE), voidAsyncResult -> {
            readyHandler.handle(Future.succeededFuture(this));
        });

        this.createTable(sqlQueries.get(ChuheSqlQuery.CREATE_USERS_TABLE), voidAsyncResult -> {
            readyHandler.handle(Future.succeededFuture(this));
        });

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


        this.createTable(sqlQueries.get(ChuheSqlQuery.CREATE_CHECKCODE_TABLE), voidAsyncResult -> {
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

                // LOGGER.info(create_sql);

                connection.execute(create_sql, create -> {
                    connection.close();
                    if (create.succeeded())
                        resultHandler.handle(Future.succeededFuture());
                    else {
                        LOGGER.error("建表错误, " + create_sql, Future.failedFuture(create.cause()));
                        resultHandler.handle(Future.failedFuture(create.cause()));
                    }
                });

            }
        });
    }


    @Fluent
    public ChuheDbService fetchProductById(Long productId, Handler<AsyncResult<JsonObject>> resultHandler) {

        String fetchProductByIdSql = sqlQueries.get(ChuheSqlQuery.GET_PRODUCT);

        LOGGER.info(fetchProductByIdSql);

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
            product.put("created_at_time", create_at.getTime());
        }

        if (last_updated != null) {
            product.put("last_updated", _DATE_FM_MINUTE.format(last_updated));
            product.put("last_updated_friendly", DatetimeHelper.getFriendlyTime(last_updated, "无"));
            product.put("last_updated_time", last_updated.getTime());
        }

        byte[] productDesc = product.getBinary("product_desc");
        byte[] productMedias = product.getBinary("product_medias");

        product.fieldNames().stream().filter(p -> {
            return !p.equals("product_medias") && !p.equals("product_desc");
        }).forEach(p -> {
            Object val = product.getValue(p);
            if (val == null || ((val instanceof String) && ((String) val).trim().isEmpty())) {
                product.put(p, "无");
            }
        });

        if (!product.containsKey("product_medias")
                || productMedias == null || (new String(productMedias)).trim().isEmpty()) {
            // product.put("product_medias", "无");
        } else {
            product.put("product_medias", new String(productMedias));
        }

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
        LOGGER.info(allProductSql);


        this.dbClient.rxQuery(allProductSql)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(product -> this.processProduct(product))
                // .sorted()
                .collect(ArrayList<JsonObject>::new, List::add)
                .subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }


    @Override
    public ChuheDbService filterProductsByName(String pName, Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        String filtrProductsByNameSql = sqlQueries.get(ChuheSqlQuery.FILTER_PRODUCTS_BY_NAME);
        LOGGER.info(filtrProductsByNameSql);

        JsonArray params = new JsonArray();

        params.add("%" + pName + "%");

        this.dbClient.rxQueryWithParams(filtrProductsByNameSql, params)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(product -> {
                    JsonObject j = this.processProduct(product);
                    return j;
                })
                // .sorted()
                .collect(ArrayList<JsonObject>::new, List::add)
                .subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }


    @Override
    public ChuheDbService createProducts(JsonObject product, Handler<AsyncResult<Long>> resultHandler) {

        String createProductSql = sqlQueries.get(ChuheSqlQuery.CREATE_PRODUCT);
        LOGGER.info(createProductSql);

        JsonArray data = new JsonArray();

        data.add(product.getValue("product_name"));

        if (Strings.emptyToNull(product.getString("product_type")) != null) {
            data.add(product.getString("product_type"));
        } else {
            data.addNull();
        }

        data.add(product.getValue("product_unit"));
        data.add(product.getValue("product_price"));

        if (Strings.emptyToNull(product.getString("product_medias")) != null) {
            data.add(product.getString("product_medias"));
        } else {
            data.addNull();
        }

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
    public ChuheDbService updateProduct(Long productId, JsonObject product, Handler<AsyncResult<Integer>> resultHandler) {

        this.fetchProductById(productId, ar -> {

            if (!ar.succeeded()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            JsonObject newProduct;
            JsonObject oldProduct = ar.result();

            if (oldProduct.fieldNames().size() == 0) {
                resultHandler.handle(Future.succeededFuture(0));
                return;
            }

            product.fieldNames().forEach(f -> {
                if (product.getValue(f) == null
                        || product.getValue(f).toString().trim().length() == 0) {
                    Object objNull = null;
                    oldProduct.put(f, objNull);
                } else {
                    oldProduct.put(f, product.getValue(f));
                }

            });

            newProduct = oldProduct;

            String updateProductSql = sqlQueries.get(ChuheSqlQuery.SAVE_PRODUCT);
            LOGGER.info(updateProductSql);


            String productType = Strings.emptyToNull(newProduct.getString("product_type"));
            String productSpecStr = Strings.emptyToNull(newProduct.getString("product_spec"));
            String productDescStr = Strings.emptyToNull(newProduct.getString("product_desc"));

            JsonArray sqlParams = new JsonArray();

            sqlParams.add(newProduct.getValue("product_name"));

            if (productType != null) {
                sqlParams.add(newProduct.getString("product_type"));
            } else {
                sqlParams.addNull();
            }

            sqlParams.add(newProduct.getValue("product_unit"));
            sqlParams.add(newProduct.getValue("product_price"));

            String productMediasField = newProduct.containsKey("product_medias_field") ? newProduct.getString("product_medias_field") : null;
            String productMedias = newProduct.containsKey("product_medias") ? newProduct.getString("product_medias") : null;

            if (productMediasField == null || productMediasField.trim().isEmpty()) {
                productMediasField = null;
            }

            if ((productMedias == null || productMedias.trim().isEmpty()) && productMediasField == null) {
                sqlParams.addNull();
            } else {

                String productMediasValue = "";

                if (productMediasField != null && productMedias != null) {
                    productMediasValue = productMediasField + "," + productMedias;
                } else if (productMediasField != null) {
                    productMediasValue = productMediasField;
                } else if (productMedias != null) {
                    productMediasValue = productMedias;
                }

                sqlParams.add(productMediasValue);
            }


            if (productSpecStr != null) {
                sqlParams.add(newProduct.getString("product_spec"));
            } else {
                sqlParams.addNull();
            }

            if (productDescStr != null) {
                sqlParams.add(newProduct.getString("product_desc"));
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
            joiner.add("?");
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
            joiner.add("?");
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

        for (int i = 0; i < orderDetailItemList.size(); i++) {

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
        LOGGER.info(createOrderSql);

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
                        conn.rxSetAutoCommit(false)
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

                                JsonArray productArray = (JsonArray) order.getValue("productList");
                                List<JsonObject> productList = new ArrayList<>();
                                // List<JsonObject> productList = (List<JsonObject>)order.getValue("productList");

                                for (int i = 0; i < productArray.size(); i++) {
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

        for (int i = 0; i < orderItems.size(); i++) {

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
        LOGGER.info(allOrdersSql);

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

    private String getProductNameById(Long productId, List<JsonObject> productList) {

        JsonObject result = productList.stream().filter(p -> {
            return p.getLong("product_id").compareTo(productId) == 0;
        }).findFirst().get();

        return result != null ? result.getString("product_name") : null;
    }

    public Single<UpdateResult> saveOrderItemsReplenish(JsonObject order, List<JsonObject> productList, List<JsonObject> orderDetailItemList) {

        String allOrdersReplenishSql = sqlQueries.get(ChuheSqlQuery.SAVE_ORDER_ITEMS_REPLENISH);

        String s = allOrdersReplenishSql.substring(allOrdersReplenishSql.lastIndexOf("("));     // (?, ?, ?, ?, ?, ?, ?)

        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < orderDetailItemList.size() - 1; i++) joiner.add(s);
        if (orderDetailItemList.size() > 1) allOrdersReplenishSql = allOrdersReplenishSql + "," + joiner.toString();

        LOGGER.info(allOrdersReplenishSql);

        JsonArray params = new JsonArray();

        for (int i = 0; i < orderDetailItemList.size(); i++) {

            JsonObject item = orderDetailItemList.get(i);

            String productName = getProductNameById(item.getLong("product_id"), productList);

            params.add(order.getValue("order_type"));
            params.add(order.getValue("order_id"));
            params.add(item.getValue("product_id"));

            if (productName != null) params.add(productName);
            else params.addNull();

            params.add(item.getValue("product_price"));
            params.add(item.getValue("product_buy_count"));

            addedParams(params, item, "order_item_desc");

        }

        Single<UpdateResult> saveOrderItemsSingle = this.dbClient.rxUpdateWithParams(allOrdersReplenishSql, params);

        return saveOrderItemsSingle;

    }


    public Single<UpdateResult> saveStocks(JsonObject order, List<JsonObject> productList, List<JsonObject> orderDetailItemList) {

        String saveStocksSql = sqlQueries.get(ChuheSqlQuery.SAVE_STOCK);
        String s = saveStocksSql.substring(saveStocksSql.lastIndexOf("("));     // (?, ?, ?, ?, ?, ?, ?)

        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < orderDetailItemList.size() - 1; i++) joiner.add(s);
        if (orderDetailItemList.size() > 1) saveStocksSql = saveStocksSql + "," + joiner.toString();

        LOGGER.info(saveStocksSql);

        JsonArray params = new JsonArray();

        for (int i = 0; i < orderDetailItemList.size(); i++) {

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
            params.add(item.getDouble("product_price"));
            params.add(count * item.getDouble("product_price"));

            params.add(0);

        }

        Single<UpdateResult> saveStockSingle = this.dbClient.rxUpdateWithParams(saveStocksSql, params);

        return saveStockSingle;

    }


    public Single<UpdateResult> saveOrderItemsSales(JsonObject order, List<JsonObject> productList, List<JsonObject> orderDetailItemList) {

        String allOrdersSalesSql = sqlQueries.get(ChuheSqlQuery.SAVE_ORDER_ITEMS_SALES);

        String s = allOrdersSalesSql.substring(allOrdersSalesSql.lastIndexOf("("));     // (?, ?, ?, ?, ?, ?, ?)

        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < orderDetailItemList.size() - 1; i++) joiner.add(s);
        if (orderDetailItemList.size() > 1) allOrdersSalesSql = allOrdersSalesSql + "," + joiner.toString();

        LOGGER.info(allOrdersSalesSql);

        JsonArray params = new JsonArray();

        for (int i = 0; i < orderDetailItemList.size(); i++) {

            JsonObject item = orderDetailItemList.get(i);

            String productName = getProductNameById(item.getLong("product_id"), productList);

            params.add(order.getValue("order_type"));
            params.add(order.getValue("order_id"));
            params.add(item.getValue("product_id"));

            if (productName != null) params.add(productName);
            else params.addNull();

            params.add(item.getValue("product_price"));
            params.add(item.getValue("product_sale_count"));

            if (!item.containsKey("order_item_desc")
                    || item.getString("order_item_desc").trim().length() == 0
                    || "null".equals(item.getString("order_item_desc").trim().toLowerCase())) {
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
                    if (orderItems.getRows().size() == 0) return new ArrayList<JsonObject>();
                    return processOrderItems(orderItems.getRows(), orderType);
                })
                .subscribe(orderIems -> {
                    if (orderIems.size() == 0) {
                        resultHandler.handle(Future.succeededFuture(new JsonObject()));
                    } else {
                        returnOrder.getJsonObject(0).put("order_items", orderIems);
                        resultHandler.handle(Future.succeededFuture(returnOrder.getJsonObject(0)));
                    }
                }, error -> {
                    resultHandler.handle(Future.failedFuture(error.getMessage()));
                });

        return this;

    }


    @Override
    public ChuheDbService fetchAllStocks(Long productId, Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        String fetchAllStocksSql = this.sqlQueries.get(ChuheSqlQuery.GET_ALL_STOCK);
        String productStocksSql = this.sqlQueries.get(ChuheSqlQuery.PRODUCT_STOCK);

        String sql = fetchAllStocksSql;

        if (productId != -1) {
            sql = productStocksSql;
        }

        LOGGER.info(sql);

        Single<ResultSet> singleQuery = null;

        if (productId == -1) {
            singleQuery = this.dbClient.rxQuery(sql);
        } else {
            singleQuery = this.dbClient.rxQueryWithParams(sql, new JsonArray().add(productId));
        }

        singleQuery.flatMapObservable(resultSet -> Observable.from(resultSet.getRows()))
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

    public String getRolesString(JsonArray userRoles) {

        StringJoiner userRolesJoiner = new StringJoiner(",");

        for (int i = 0; i < userRoles.size(); i++) {
            userRolesJoiner.add(userRoles.getString(i));
        }

        return userRolesJoiner.toString();

    }

    @Override
    public ChuheDbService createCustomer(JsonObject user, JsonArray userRoles, Handler<AsyncResult<Long>> resultHandler) {
        return this.createUser(user, null, null, null, userRoles, resultHandler);
    }


    @Override
    public ChuheDbService createUserBeforeCheck(JsonObject user, String check_code, String check_code_sign, String validate_code_sign, Handler<AsyncResult<Boolean>> resultHandler) {

        String validateCheckCodeSql = this.sqlQueries.get(ChuheSqlQuery.VALIDATE_CHECKCODE);

        LOGGER.info("ChuheSqlQuery.VALIDATE_CHECKCODE: " + validateCheckCodeSql);

        JsonArray validateCheckCodeParam = new JsonArray();

        validateCheckCodeParam.add(validate_code_sign);
        validateCheckCodeParam.add(check_code);
        // validateCheckCodeParam.add(check_code_sign);

        this.dbClient
                .rxQuerySingleWithParams(validateCheckCodeSql, validateCheckCodeParam)
                .subscribe(resultSet -> {
                    resultHandler.handle(Future.succeededFuture(!resultSet.isEmpty()));
                }, error -> {
                    resultHandler.handle(Future.failedFuture(error.getMessage()));
                });

        return this;
    }

    @Override
    public ChuheDbService createUser(JsonObject user, String check_code, String check_code_sign, String validate_code_sign, JsonArray userRoles, Handler<AsyncResult<Long>> resultHandler) {


        String createUserSql = this.sqlQueries.get(ChuheSqlQuery.SAVE_USER);

        LOGGER.info("ChuheSqlQuery.SAVE_USER: " + createUserSql);


        JsonArray createUserParams = new JsonArray();
        addParams(user, createUserParams, "user_name", true);
        addParams(user, createUserParams, "user_passwd", true);
        addParams(user, createUserParams, "user_name", true);
        addParams(user, createUserParams, "user_gender", true);
        addParams(user, createUserParams, "user_identity", true);
        addParams(user, createUserParams, "user_phone", true);
        addParams(user, createUserParams, "user_home_tel", true);
        addParams(user, createUserParams, "home_address", true);
        addParams(user, createUserParams, "wchat_id", true);
        addParams(user, createUserParams, "user_source_from", true);
        createUserParams.add(getRolesString(userRoles));


        this.dbClient.rxUpdateWithParams(createUserSql, createUserParams)
                .subscribe(updateResult -> {
                    resultHandler.handle(Future.succeededFuture(updateResult.getKeys().getLong(0)));
                }, error -> {
                    resultHandler.handle(Future.failedFuture(error.getMessage()));

                });


        return this;
    }

    public void rxError(Handler<AsyncResult<String>> resultHandler) {
        resultHandler.handle(Future.succeededFuture("错误...."));
    }


        @Override
    public ChuheDbService deleteUserBatch(List<Long> userIdList, Handler<AsyncResult<Integer>> resultHandler) {

        String deleteUserSqlBatch = sqlQueries.get(ChuheSqlQuery.DELETE_USER_BATCH);

        JsonArray sqlParam = new JsonArray();
        StringJoiner joiner = new StringJoiner(",");

        userIdList.forEach(dealerId -> {
            sqlParam.add(dealerId);
            joiner.add("?");
        });

        deleteUserSqlBatch = deleteUserSqlBatch.replaceAll(
                "_user_id_list_", joiner.toString());

        StringBuilder deleteUserSqlBatchBuilder = new StringBuilder(deleteUserSqlBatch);

        LOGGER.info(deleteUserSqlBatch);

        this.dbClient.rxGetConnection()
                .flatMap(conn ->
                        conn
                                .rxSetAutoCommit(false)
                                // delete user
                                .flatMap(autoCommit -> {
                                    return conn.rxUpdateWithParams(deleteUserSqlBatchBuilder.toString(), sqlParam);
                                })
                                .flatMap(updateResult -> {
                                    return conn.rxCommit().map(commit -> updateResult.getUpdated());
                                })
                                .onErrorResumeNext(ex ->
                                        conn.rxRollback()
                                                .onErrorResumeNext(ex2 ->
                                                        Single.error(new CompositeException(ex, ex2))
                                                ).flatMap(ignore -> Single.error(ex))
                                ).doAfterTerminate(conn::close)
                ).subscribe(affectRowCount -> {
            resultHandler.handle(Future.succeededFuture(affectRowCount));
        }, error -> {

            resultHandler.handle(Future.failedFuture(error.getMessage()));
        });


        return this;
    }


    @Override
    public ChuheDbService userDetail(Long userId, Handler<AsyncResult<JsonObject>> resultHandler) {

        String userDetailSql = this.sqlQueries.get(ChuheSqlQuery.GET_USER);

        LOGGER.info(userDetailSql);

        JsonArray params = new JsonArray();

        params.add(userId);

        Single<JsonObject> result = this.dbClient.rxQueryWithParams(userDetailSql, params)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(user -> processEmptyFields(user))
                .map(user -> processGender(user, "user_gender"))
                .defaultIfEmpty(new JsonObject()).toSingle();


        result.subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }


    private void addedParams(JsonArray params, JsonObject model, String field) {
        if (!model.containsKey(field) || model.getValue(field) == null || model.getValue(field).toString().trim().length() == 0
                || model.getValue(field).toString().toLowerCase().trim().equals("null")) {
            params.addNull();
        } else {
            params.add(model.getValue(field));
        }
    }

    public JsonArray addParams(JsonObject data, JsonArray params, String key) {
        return addParams(data, params, key, false);
    }


    public JsonArray addParams(JsonObject data, JsonArray params, String key, boolean nullIfEmpty) {

        if (key == null || !data.containsKey(key) || data.getValue(key) == null) {
            params.addNull();
            return params;
        }

        if (!nullIfEmpty) {
            params.add(data.getValue(key));
            return params;
        }

        if (data.getString(key).trim().isEmpty())
            params.addNull();
        else params.add(data.getString(key).trim());

        return params;
    }

    @Override
    public ChuheDbService createDealer(JsonObject dealer, JsonArray userRoles, Handler<AsyncResult<Long>> resultHandler) {

        String createUserSql = this.sqlQueries.get(ChuheSqlQuery.SAVE_USER);
        String createDealerSql = this.sqlQueries.get(ChuheSqlQuery.SAVE_DEALER);

        LOGGER.info(createUserSql);
        LOGGER.info(createDealerSql);

        JsonArray userParams = new JsonArray();
        JsonArray dealerParams = new JsonArray();

        addParams(dealer, userParams, "dealer_name");       // user_name
        addParams(dealer, userParams, null, true);                // user_passwd
        addParams(dealer, userParams, "dealer_name");       // user_real_name
        addParams(dealer, userParams, "dealer_gender", true);
        addParams(dealer, userParams, "dealer_identity", true);
        addParams(dealer, userParams, "dealer_phone", true);
        addParams(dealer, userParams, "dealer_home_tel", true);
        addParams(dealer, userParams, "dealer_home_address", true);
        addParams(dealer, userParams, "dealer_wchat_id", true);
        addParams(dealer, userParams, "dealer_source_from", true);
        userParams.add(getRolesString(userRoles));


        this.dbClient.rxGetConnection()
                .flatMap(conn ->
                        conn
                                .rxSetAutoCommit(false)
                                // insert user
                                .flatMap(autoCommit -> conn.rxUpdateWithParams(createUserSql, userParams))
                                // get user id
                                .flatMap(updateResult -> {
                                    Long newUserId = updateResult.getKeys().getLong(0);
                                    dealer.put("user_id", newUserId);

                                    addParams(dealer, dealerParams, "user_id");
                                    addParams(dealer, dealerParams, "dealer_name", true);
                                    addParams(dealer, dealerParams, "dealer_level", true);
                                    addParams(dealer, dealerParams, "dealer_scope", true);
                                    addParams(dealer, dealerParams, "dealer_desc", true);

                                    return conn.rxUpdateWithParams(createDealerSql, dealerParams);
                                }).onErrorResumeNext(ex ->
                                conn.rxRollback()
                                        .onErrorResumeNext(ex2 ->
                                                Single.error(new CompositeException(ex, ex2))
                                        ).flatMap(ignore -> Single.error(ex))
                        ).doAfterTerminate(conn::close)
                ).subscribe(updateResult -> {
            resultHandler.handle(Future.succeededFuture(dealer.getLong("user_id")));
        }, error -> {

            resultHandler.handle(Future.failedFuture(error.getMessage()));
        });

        return this;
    }

    public JsonObject processGender(JsonObject o, String key) {

        switch (o.getString(key)) {
            case "MAN":
                o.put(key, "男");
                break;
            case "WOMEN":
                o.put(key, "女");
                break;
            default:
                //dealer.put("dealer_gender", dealer.getString("dealer_gender") + "(未知)");
        }

        return o;
    }

    @Override
    public ChuheDbService dealerDetail(Long dealerId, Handler<AsyncResult<JsonObject>> resultHandler) {

        String dealerDetailsSql = this.sqlQueries.get(ChuheSqlQuery.GET_DEALER);

        LOGGER.info(dealerDetailsSql);

        JsonArray params = new JsonArray();

        params.add(dealerId);

        Single<JsonObject> result = this.dbClient.rxQueryWithParams(dealerDetailsSql, params)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(dealer -> processEmptyFields(dealer))
                .map(dealer -> processGender(dealer, "dealer_gender"))

                //.map(dealer -> this.processProduct(dealer))
                .defaultIfEmpty(new JsonObject()).toSingle();


        result.subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }

    private JsonObject processEmptyFields(JsonObject o) {
        o.fieldNames().forEach(fname -> {
            if (o.getValue(fname) == null) {
                o.put(fname, "无");
            }
        });
        return o;
    }

    @Override
    public ChuheDbService fetchAllUsers(Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        String fetchAllUsersSql = this.sqlQueries.get(ChuheSqlQuery.ALL_USERS);

        LOGGER.info(fetchAllUsersSql);

        this.dbClient.rxQuery(fetchAllUsersSql)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(user -> processEmptyFields(user))
                .map(user -> processGender(user, "user_gender"))
                .collect(ArrayList<JsonObject>::new, List::add)
                .subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }


    @Override
    public ChuheDbService updateUserRoles(Long userId, String userRoles, Handler<AsyncResult<Integer>> resultHandler) {

        String updateUserRolesSql = sqlQueries.get(ChuheSqlQuery.UPDATE_USER_ROLES);

        LOGGER.info(updateUserRolesSql);

        JsonArray params = new JsonArray();

        params.add(userRoles).add(userId);

        this.dbClient.rxUpdateWithParams(updateUserRolesSql, params)
                .subscribe(updateResult -> {
                    resultHandler.handle(Future.succeededFuture(updateResult.getUpdated()));
                }, error -> {
                    resultHandler.handle(Future.failedFuture(error.getMessage()));

                });

        return this;
    }

    @Override
    public ChuheDbService validateUser(String username, String passwd, Handler<AsyncResult<Boolean>> resultHandler) {

        if (null != username && null != passwd
                && !username.trim().isEmpty()
                && !passwd.isEmpty()) {

        }

        return this;
    }

    @Override
    public ChuheDbService updateUser(Long userId, JsonObject newUser, Handler<AsyncResult<Integer>> resultHandler) {


        this.userDetail(userId, ar -> {

            if (!ar.succeeded()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            JsonObject customer;
            JsonObject oldCustomer = ar.result();

            if (oldCustomer.fieldNames().size() == 0) {
                resultHandler.handle(Future.succeededFuture(0));
                return;
            }

            newUser.fieldNames().forEach(f -> {
                oldCustomer.put(f, newUser.getValue(f));
            });

            customer = oldCustomer;

            String updateUserSql = sqlQueries.get(ChuheSqlQuery.UPDATE_USER);

            LOGGER.info(updateUserSql);

            JsonArray sqlParamsUser = new JsonArray();


            addParams(customer, sqlParamsUser, "user_name", true);
            addParams(customer, sqlParamsUser, "user_gender", true);
            addParams(customer, sqlParamsUser, "user_identity", true);
            addParams(customer, sqlParamsUser, "user_source_from", true);
            addParams(customer, sqlParamsUser, "user_phone", true);
            addParams(customer, sqlParamsUser, "user_home_tel", true);
            addParams(customer, sqlParamsUser, "home_address", true);
            addParams(customer, sqlParamsUser, "wchat_id", true);


            sqlParamsUser.add(userId);


            this.dbClient.rxGetConnection()
                    .flatMap(conn ->
                            conn
                                    .rxSetAutoCommit(false)
                                    // update user
                                    .flatMap(autoCommit -> {
                                        return conn.rxUpdateWithParams(updateUserSql, sqlParamsUser);
                                    })
                                    .flatMap(updateResult -> {
                                        return conn.rxCommit().map(commit -> updateResult.getUpdated());
                                    })
                                    .onErrorResumeNext(ex ->
                                            conn.rxRollback()
                                                    .onErrorResumeNext(ex2 ->
                                                            Single.error(new CompositeException(ex, ex2))
                                                    ).flatMap(ignore -> Single.error(ex))
                                    ).doAfterTerminate(conn::close)
                    ).subscribe(affectRowCount -> {
                resultHandler.handle(Future.succeededFuture(affectRowCount));
            }, error -> {

                resultHandler.handle(Future.failedFuture(error.getMessage()));
            });


        });


        return this;

    }


    @Override
    public ChuheDbService fetchAllDealers(Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        String fetchAllDealersSql = this.sqlQueries.get(ChuheSqlQuery.ALL_DEALERS);

        LOGGER.info(fetchAllDealersSql);

        this.dbClient.rxQuery(fetchAllDealersSql)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(user -> processEmptyFields(user))
                .map(dealer -> processGender(dealer, "dealer_gender"))
                .collect(ArrayList<JsonObject>::new, List::add)
                .subscribe(RxHelper.toSubscriber(resultHandler));

        return this;
    }

    @Override
    public ChuheDbService deleteDealerBatch(List<Long> dealerIdList, Handler<AsyncResult<Integer>> resultHandler) {

        String deleteUserSqlBatch = sqlQueries.get(ChuheSqlQuery.DELETE_USER_BATCH);
        String deleteDealerSqlBatch = sqlQueries.get(ChuheSqlQuery.DELETE_DEALER_BATCH);

        JsonArray sqlParam = new JsonArray();
        StringJoiner joiner = new StringJoiner(",");

        dealerIdList.forEach(dealerId -> {
            sqlParam.add(dealerId);
            joiner.add("?");
        });

        deleteUserSqlBatch = deleteUserSqlBatch.replaceAll(
                "_user_id_list_", joiner.toString());

        deleteDealerSqlBatch = deleteDealerSqlBatch.replaceAll(
                "_dealer_id_list_", joiner.toString());

        StringBuilder deleteUserSqlBatchBuilder = new StringBuilder(deleteUserSqlBatch);
        StringBuilder deleteDealerSqlBatchBuilder = new StringBuilder(deleteDealerSqlBatch);

        LOGGER.info(deleteUserSqlBatch);
        LOGGER.info(deleteDealerSqlBatch);

        this.dbClient.rxGetConnection()
                .flatMap(conn ->
                        conn
                                .rxSetAutoCommit(false)
                                // update user
                                .flatMap(autoCommit -> {
                                    return conn.rxUpdateWithParams(deleteUserSqlBatchBuilder.toString(), sqlParam);
                                })
                                // update dealer
                                .flatMap(updateResult -> {
                                    return conn.rxUpdateWithParams(deleteDealerSqlBatchBuilder.toString(), sqlParam);
                                })
                                .flatMap(updateResult -> {
                                    return conn.rxCommit().map(commit -> updateResult.getUpdated());
                                })
                                .onErrorResumeNext(ex ->
                                        conn.rxRollback()
                                                .onErrorResumeNext(ex2 ->
                                                        Single.error(new CompositeException(ex, ex2))
                                                ).flatMap(ignore -> Single.error(ex))
                                ).doAfterTerminate(conn::close)
                ).subscribe(affectRowCount -> {
            resultHandler.handle(Future.succeededFuture(affectRowCount));
        }, error -> {

            resultHandler.handle(Future.failedFuture(error.getMessage()));
        });


        return this;
    }

    @Override
    public ChuheDbService updateDealer(Long dealerId, JsonObject newDealer, Handler<AsyncResult<Integer>> resultHandler) {


        this.dealerDetail(dealerId, ar -> {

            if (!ar.succeeded()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            JsonObject dealer;
            JsonObject oldDealer = ar.result();

            if (oldDealer.fieldNames().size() == 0) {
                resultHandler.handle(Future.succeededFuture(0));
                return;
            }

            newDealer.fieldNames().forEach(f -> {
                oldDealer.put(f, newDealer.getValue(f));
            });

            dealer = oldDealer;

            String updateUserSql = sqlQueries.get(ChuheSqlQuery.UPDATE_USER);
            String updateDealerSql = sqlQueries.get(ChuheSqlQuery.UPDATE_DEALER);

            LOGGER.info(updateUserSql);

            JsonArray sqlParamsUser = new JsonArray();
            JsonArray sqlParamsDealer = new JsonArray();


            addParams(dealer, sqlParamsUser, "dealer_name", true);
            addParams(dealer, sqlParamsUser, "dealer_gender", true);
            addParams(dealer, sqlParamsUser, "dealer_identity", true);
            addParams(dealer, sqlParamsUser, "dealer_source_from", true);
            addParams(dealer, sqlParamsUser, "dealer_phone", true);
            addParams(dealer, sqlParamsUser, "dealer_home_tel", true);
            addParams(dealer, sqlParamsUser, "dealer_home_address", true);
            addParams(dealer, sqlParamsUser, "dealer_wchat_id", true);

            addParams(dealer, sqlParamsDealer, "dealer_name", true);
            addParams(dealer, sqlParamsDealer, "dealer_level", true);
            addParams(dealer, sqlParamsDealer, "dealer_scope", true);
            addParams(dealer, sqlParamsDealer, "dealer_desc", true);


            sqlParamsUser.add(dealerId);
            sqlParamsDealer.add(dealerId);


            this.dbClient.rxGetConnection()
                    .flatMap(conn ->
                            conn
                                    .rxSetAutoCommit(false)
                                    // update user
                                    .flatMap(autoCommit -> {
                                        return conn.rxUpdateWithParams(updateUserSql, sqlParamsUser);
                                    })
                                    // update dealer
                                    .flatMap(updateResult -> {
                                        return conn.rxUpdateWithParams(updateDealerSql, sqlParamsDealer);
                                    })
                                    .flatMap(updateResult -> {
                                        return conn.rxCommit().map(commit -> updateResult.getUpdated());
                                    })
                                    .onErrorResumeNext(ex ->
                                            conn.rxRollback()
                                                    .onErrorResumeNext(ex2 ->
                                                            Single.error(new CompositeException(ex, ex2))
                                                    ).flatMap(ignore -> Single.error(ex))
                                    ).doAfterTerminate(conn::close)
                    ).subscribe(affectRowCount -> {
                resultHandler.handle(Future.succeededFuture(affectRowCount));
            }, error -> {

                resultHandler.handle(Future.failedFuture(error.getMessage()));
            });


        });


        return this;

    }


    // check_code
    @Override
    public ChuheDbService createCheckCode(JsonObject checkcode, Integer seconds, Handler<AsyncResult<Long>> resultHandler) {

        Calendar now = Calendar.getInstance();

        if (!checkcode.containsKey("created_at")) {
            checkcode.put("created_at", _DATE_FM_T.format(now.getTime()));
        }


        if (seconds != null) {
            now.add(Calendar.SECOND, seconds);
            checkcode.put("expired_at", _DATE_FM_T.format(now.getTime()));
        }

        Single<UpdateResult> updateResultSingle = this.createCheckCodeSingle(checkcode, seconds);

        updateResultSingle.subscribe(updateResult -> {
                    resultHandler.handle(Future.succeededFuture(updateResult.getKeys().getLong(0)));
                }, error -> {
                    resultHandler.handle(Future.failedFuture(error.getMessage()));

                });

        return this;
    }

    public Single<UpdateResult> createCheckCodeSingle(JsonObject checkcode, Integer seconds) {

        String saveCheckCodeSql = sqlQueries.get(ChuheSqlQuery.SAVE_CHECKCODE);

        LOGGER.info(saveCheckCodeSql);

        JsonArray data = new JsonArray();

        data.add(checkcode.getString("code_sign"));
        data.add(checkcode.getString("code_value"));
        data.add(checkcode.getString("send_channel"));

        if (checkcode.containsKey("receiver")
                && checkcode.getString("receiver") != null) {
            data.add(checkcode.getString("receiver"));
        } else {
            data.add("");
        }

        data.add(checkcode.getString("client_ip"));
        data.add(checkcode.getString("client_agent"));
        data.add(checkcode.getString("created_at"));

        if (checkcode.containsKey("expired_at")) {
            data.add(checkcode.getString("expired_at"));
        } else {
            data.addNull();
        }

        Single<UpdateResult> updateResultSingle = this.dbClient.rxUpdateWithParams(saveCheckCodeSql, data);

        return updateResultSingle;

    }

    @Override
    public ChuheDbService validateCheckCodePhoneOrEmail(String sign, String code, String receiver, String checktype, String client_ip, String client_agent, Handler<AsyncResult<JsonObject>> resultHandler) {

        String validateCheckCodeSql = sqlQueries.get(ChuheSqlQuery.CONFIRM_CHECKCODE);

        LOGGER.info(validateCheckCodeSql);

        JsonArray data = new JsonArray();

        data.add(sign);
        data.add(code);
        // data.add(receiver);

        this.dbClient.rxQueryWithParams(validateCheckCodeSql, data).subscribe(resultSet -> {

            JsonObject jsonObject = getCheckCode(resultSet);

            if (jsonObject != null) {
                this.updateCheckCodeConfirmTime(sign, receiver, checktype, reply -> {
                    if (reply.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(jsonObject));
                    } else {
                        resultHandler.handle(Future.succeededFuture(null));
                    }
                });
            } else {
                resultHandler.handle(Future.succeededFuture(jsonObject));
            }

        }, error -> {
            resultHandler.handle(Future.failedFuture(error.getMessage()));

        });

        return this;

    }

    private JsonObject getCheckCode(ResultSet resultSet) {

        if (resultSet.getRows().size() == 0) return null;

        JsonObject returnCheckCoce = null;

        if (resultSet.getRows().get(0).getString("expired_at") == null) {
            // 无过期时间，即长期有效
            returnCheckCoce = resultSet.getRows().get(0);
        } else {

            Date expired = null;

            try {
                expired = _DATE_FM_T.parse(resultSet.getRows().get(0).getString("expired_at"));
            } catch (ParseException e) {

            }

            if (expired == null) {
                returnCheckCoce = resultSet.getRows().get(0);
            }

            if (expired != null && expired.compareTo(Calendar.getInstance().getTime()) == 1) {
                returnCheckCoce = resultSet.getRows().get(0);
            }
        }

        return returnCheckCoce;

    }

    @Override
    public ChuheDbService validateCheckCodeImage(String sign, String code, String receiver, Integer expiredSeconds, String checktype, String client_ip, String client_agent, Handler<AsyncResult<JsonObject>> resultHandler) {


        String validateCheckCodeSql = sqlQueries.get(ChuheSqlQuery.CONFIRM_CHECKCODE);

        LOGGER.info(validateCheckCodeSql);

        JsonArray data = new JsonArray();

        data.add(sign);
        data.add(code);
        // data.add(receiver);

        JsonObject newCheckcode = new JsonObject();
        JsonObject checkCodeFinded = new JsonObject();


        // https://github.com/vert-x3/vertx-examples/blob/master/rxjava-1-examples/src/main/java/io/vertx/example/rxjava/database/jdbc/Transaction.java
        this.dbClient.rxGetConnection()
                .flatMap(conn ->
                        conn.rxSetAutoCommit(false)
                                // select check code
                                .flatMap(autoCommit -> conn.rxQueryWithParams(validateCheckCodeSql, data))
                                // process result set
                                .map(resultSet -> {
                                    return getCheckCode(resultSet);
                                })
                                // insert into email check code
                                .flatMap(returnCheckCoce -> {

                                    if (returnCheckCoce != null) {
                                        // 验证成功, 图片验证成功后，插入一条短信或邮箱验证码
                                        newCheckcode.put("code_sign", RandomStringUtils.randomAlphanumeric(48));
                                        newCheckcode.put("code_value", RandomStringUtils.randomNumeric(4));
                                        newCheckcode.put("receiver", receiver);
                                        newCheckcode.put("send_channel", MyValidate.validateEmail(receiver) ? "email" : "phone");  // or phone
                                        newCheckcode.put("client_ip", client_ip);
                                        newCheckcode.put("client_agent", client_agent);

                                        Calendar now = Calendar.getInstance();
                                        newCheckcode.put("created_at", _DATE_FM_T.format(now.getTime()));

                                        if (expiredSeconds != null) {
                                            now.add(Calendar.SECOND, expiredSeconds);
                                            newCheckcode.put("expired_at", _DATE_FM_T.format(now.getTime()));
                                        }

                                        return createCheckCodeSingle(newCheckcode, expiredSeconds);

                                    }

                                    return null;

                                })
                                // update confirm time
                                .flatMap(returnCheckCoce -> {

                                    // convert time string to long

                                    try {

                                        newCheckcode.put("created_at", _DATE_FM_T.parse(newCheckcode.getString("created_at")).getTime());

                                        if (newCheckcode.containsKey("expired_at") && newCheckcode.getString("expired_at") != null) {
                                            newCheckcode.put("expired_at", _DATE_FM_T.parse(newCheckcode.getString("expired_at")).getTime());
                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (returnCheckCoce != null) {
                                        return updateCheckCodeConfirmTimeSingle(sign, receiver, checktype);
                                    }

                                    return null;
                                })
                                .flatMap(updateResult -> {
                                    return conn.rxCommit().map(commit -> updateResult);
                                })
                                .onErrorResumeNext(ex -> {

                                    return conn.rxRollback()
                                            .onErrorResumeNext(ex2 ->
                                                    Single.error(new CompositeException(ex, ex2))
                                            ).flatMap(ignore -> Single.error(ex));

                                }).doAfterTerminate(conn::close)
                ).subscribe(updateResult -> {
            resultHandler.handle(Future.succeededFuture(newCheckcode));
        }, error -> {
            if (checkCodeFinded.containsKey("code_sign")) {
                resultHandler.handle(Future.succeededFuture(checkCodeFinded));
            } else {
                resultHandler.handle(Future.failedFuture(error.getMessage()));
            }

        });

        return this;
    }

    @Override
    public ChuheDbService validateCheckCode(String sign, String code, String receiver, Handler<AsyncResult<String>> resultHandler) {

        String validateCheckCodeSql = sqlQueries.get(ChuheSqlQuery.CONFIRM_CHECKCODE);

        LOGGER.info(validateCheckCodeSql);

        JsonArray data = new JsonArray();

        data.add(sign);
        data.add(code);
        // data.add(receiver);

        Single<String> result = this.dbClient
                .rxQueryWithParams(validateCheckCodeSql, data)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(checkcode -> {

                    if (checkcode.getString("expired_at") == null) {
                        // 无过期时间，即长期有效
                        // 更新 receiver
                        return checkcode.getString("send_channel");
                    }

                    Date expired = null;

                    try {
                        expired = _DATE_FM_T.parse(checkcode.getString("expired_at"));
                    } catch (ParseException e) {

                    }

                    if (expired == null) {
                        // 更新 receiver
                        return checkcode.getString("send_channel");
                    }

                    if (expired.compareTo(Calendar.getInstance().getTime()) == 1) {
                        // 更新 receiver
                        return checkcode.getString("send_channel");
                    } else {
                        return null;
                    }

                })
                .defaultIfEmpty(null).toSingle();

        result.subscribe(RxHelper.toSubscriber(resultHandler));

        return this;

    }

    public Single<UpdateResult> updateCheckCodeConfirmTimeSingle(String sign, String receiver, String channel) {

        String updateConfirmTimeSql = sqlQueries.get(ChuheSqlQuery.UPDATE_CONFIRM_TIME);

        LOGGER.info(updateConfirmTimeSql);

        JsonArray data = new JsonArray();

        data.add(_DATE_FM_T.format(Calendar.getInstance().getTime()));
        data.add(receiver);
        data.add(sign);

        if (channel == null) {
            data.addNull();
        } else {
            data.add(channel);
        }

        return this.dbClient.rxUpdateWithParams(updateConfirmTimeSql, data);

    }


    @Override()
    public ChuheDbService updateCheckCodeConfirmTime(String sign, String receiver, String channel, Handler<AsyncResult<Long>> resultHandler) {

        Single<UpdateResult> updateResultSingle = updateCheckCodeConfirmTimeSingle(sign, receiver, channel);

        updateResultSingle.subscribe(updateResult -> {
            resultHandler.handle(Future.succeededFuture(updateResult.getKeys().getLong(0)));
        }, error -> {
            resultHandler.handle(Future.failedFuture(error.getMessage()));

        });

        return this;

    }

}
