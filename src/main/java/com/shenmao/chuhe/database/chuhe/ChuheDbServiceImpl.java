package com.shenmao.chuhe.database.chuhe;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.SQLClient;
import io.vertx.rx.java.RxHelper;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChuheDbServiceImpl implements ChuheDbService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ChuheDbServiceImpl.class);
    private final HashMap<ChuheSqlQuery, String> sqlQueriesPages;
    private final SQLClient dbClient;

    ChuheDbServiceImpl(SQLClient dbClient, HashMap<ChuheSqlQuery, String> sqlQueries,
                       Handler<AsyncResult<ChuheDbService>> readyHandler) {

        this.dbClient = dbClient;
        this.sqlQueriesPages = sqlQueries;

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
                SQLConnection connection = ar.result();
                connection.execute(sqlQueriesPages.get(ChuheSqlQuery.CREATE_PRODUCTS_TABLE), create -> {
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
    public ChuheDbService fetchAllProducts(Handler<AsyncResult<List<JsonObject>>> resultHandler) {


        JsonObject p1 = new JsonObject()
                .put("wiki_page_title", "firt wiki page")
                .put("wiki_page_id", 1)
                .put("markdown", "markdown 1")
                .put("page_content", "page_content 1");

        JsonObject p2 = new JsonObject()
                .put("wiki_page_title", "second wiki page")
                .put("wiki_page_id", 2)
                .put("markdown", "markdown 1")
                .put("page_content", "page_content 2");

        List<JsonObject> list = new ArrayList<>();

        list.add(p1);
        list.add(p2);
        list.stream().sorted();

//        this.dbClient.rxQuery(sqlQueriesPages.get(ChuheSqlQuery.ALL_PRODUCTS))
//                .map(a -> {
//                    System.out.println(a.getRows().size() + ",,, fetchAllProducts 1");
//                    return list;
//                })
//                .subscribe(RxHelper.toSubscriber(resultHandler));

        resultHandler.handle(Future.succeededFuture(list));

        return this;
    }
}
