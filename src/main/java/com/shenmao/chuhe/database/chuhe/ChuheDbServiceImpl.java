package com.shenmao.chuhe.database.chuhe;

import com.google.common.base.Strings;
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
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChuheDbServiceImpl implements ChuheDbService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ChuheDbServiceImpl.class);
    private final HashMap<ChuheSqlQuery, String> sqlQueries;
    private final SQLClient dbClient;

    ChuheDbServiceImpl(SQLClient dbClient, HashMap<ChuheSqlQuery, String> sqlQueries,
                       Handler<AsyncResult<ChuheDbService>> readyHandler) {

        this.dbClient = dbClient;
        this.sqlQueries = sqlQueries;

        this.createProductsTable(voidAsyncResult -> {
            readyHandler.handle(Future.succeededFuture(this));
        });

    }


    public void createProductsTable(Handler<AsyncResult<Void>> resultHandler) {

        dbClient.getConnection(ar -> {
            if (ar.failed()) {
                LOGGER.error("Could not open Chuhe database connection", ar.cause());
                resultHandler.handle(Future.failedFuture(ar.cause()));
            } else {
                SQLConnection connection = ar.result().getDelegate();
                connection.execute(sqlQueries.get(ChuheSqlQuery.CREATE_PRODUCTS_TABLE), create -> {
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


    @Override
    public ChuheDbService lastIncrementId(Handler<AsyncResult<Long>> resultHandler) {

        this.dbClient.rxQuerySingle(sqlQueries.get(ChuheSqlQuery.LAST_INSERT_ID))
                .map(a -> a.getLong(0))
                .subscribe(RxHelper.toSubscriber(resultHandler));
        return this;
    }

    @Override
    public ChuheDbService fetchAllProducts(Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        String allProductSql = sqlQueries.get(ChuheSqlQuery.ALL_PRODUCTS);
        LOGGER.info( allProductSql);

        this.dbClient.rxQuery(allProductSql)
                .flatMapObservable(res -> Observable.from(res.getRows()))
                .map(product -> {

                    byte[] productDesc = product.getBinary("product_desc");

                    if (productDesc != null) {
                        return product.put("product_desc", new String(productDesc));
                    } else {
                        return product.put("product_desc", productDesc);
                    }

                })
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
                .add(product.getString("productName"))
                .add(product.getString("productUnit"))
                .add(product.getDouble("productPrice"));

        if (Strings.emptyToNull(product.getString("productSpec")) == null) {
            data.addNull();
        }

        if (Strings.emptyToNull(product.getString("productDesc")) == null) {
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

}
