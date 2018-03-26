package com.shenmao.chuhe.verticle;

import com.shenmao.chuhe.Application;
import com.shenmao.chuhe.commons.PropertyParser;
import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.database.chuhe.sqlqueries.ChuheSqlQuery;
import com.shenmao.chuhe.database.chuhe.sqlqueries.DbQueryHelper;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.rxjava.ext.asyncsql.MySQLClient;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.sql.SQLClient;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class ChuheDbVerticle extends AbstractVerticle {

    public static final String CONFIG_CHUHEDB_JDBC_URL = "chuhedb.jdbc.url";
    public static final String CONFIG_CHUHEDB_JDBC_DRIVER_CLASS = "chuhedb.jdbc.driver_class";
    public static final String CONFIG_CHUHEDB_JDBC_MAX_POOL_SIZE = "chuhedb.jdbc.max_pool_size";
    public static final String CONFIG_CHUHEDB_SQL_QUERIES_RESOURCE_FILE = "chuhedb.sqlqueries.resource.file";
    public static final String CONFIG_CHUHEDB_QUEUE = "chuhedb.queue";
    private static final Logger LOGGER = LoggerFactory.getLogger(ChuheDbVerticle.class);

    private SQLClient dbClient;


    public void mongodb(JsonObject config) {

        String uri = config.getString("mongo_uri", "mongodb://localhost:27017");
        String db = config.getString("mongo_db_name", "vertx_db");


        JsonObject mongoconfig = new JsonObject()
                .put("connection_string", uri)
                .put("db_name", db);

        MongoClient mongoClient = MongoClient.createShared(vertx.getDelegate(), mongoconfig);

        JsonObject product1 = new JsonObject().put("itemId", "12345").put("name", "Cooler").put("price", "100.0");

        mongoClient.save("products", product1, id -> {
            System.out.println("Inserted id: " + id.result());

            mongoClient.find("products", new JsonObject().put("itemId", "12345"), res -> {
                System.out.println("Name is " + res.result().get(0).getString("name"));

            /* mongoClient.remove("products", new JsonObject().put("itemId", "12345"), rs -> {
              if (rs.succeeded()) {
                System.out.println("Product removed ");
              }
            }); */

            });

        });
    }

    public SQLClient mysql(JsonObject config) {


        String dbHost = Application.getConfig().getString("mysql_host");
        Integer dbPort = Application.getConfig().getInteger("mysql_port");
        String dbName = Application.getConfig().getString("mysql_database_name");
        String dbUname = Application.getConfig().getString("mysql_uname");
        String dbPasswd = Application.getConfig().getString("mysql_password");

        JsonObject mySQLClientConfig = new JsonObject()
                .put("host", dbHost)
                .put("port", dbPort)
                .put("database", dbName)
                .put("driver_class", config().getString(CONFIG_CHUHEDB_JDBC_DRIVER_CLASS, "com.mysql.jdbc.Driver"))
                .put("maxPoolSize", config().getInteger(CONFIG_CHUHEDB_JDBC_MAX_POOL_SIZE, 30))
                .put("queryTimeout", 20000)     // 20 seconds
                .put("charset", "UTF-8")
                .put("username", dbUname)
                .put("password", dbPasswd);

//        SQLClient mySQLClient  = MySQLClient.createShared(vertx.getDelegate(), mySQLClientConfig);

        SQLClient mySQLClient  = MySQLClient.createShared(vertx, mySQLClientConfig);

        return mySQLClient;

    }


    @Override
    public void start(Future<Void> startFuture) throws Exception {

//        mongodb(vertx.currentContext().config());

        dbClient = mysql(vertx.currentContext().config());


        ChuheDbService.create(dbClient, loadSqlQueries(), ready -> {

            if (ready.succeeded()) {

                (new ServiceBinder(vertx.getDelegate())).setAddress(CONFIG_CHUHEDB_QUEUE).register(ChuheDbService.class, ready.result());

                startFuture.complete();

            } else {

                startFuture.fail(ready.cause());
            }
        });

    }

    public HashMap<ChuheSqlQuery, String> loadSqlQueries() throws IOException {

        return DbQueryHelper.getSqlQueries();
    }



}
