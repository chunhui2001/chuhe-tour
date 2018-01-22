package com.shenmao.chuhe.verticle;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.database.chuhe.ChuheSqlQuery;
import com.shenmao.chuhe.database.wikipage.WikiPageDbService;
import com.shenmao.chuhe.database.wikipage.WikiSqlQuery;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

        JsonObject mySQLClientConfig = new JsonObject()
                .put("host", "127.0.0.1")
                .put("port", 3306)
                .put("database", "db_chuhe_local")
                .put("driver_class", config().getString(CONFIG_CHUHEDB_JDBC_DRIVER_CLASS, "com.mysql.jdbc.Driver"))
                .put("maxPoolSize", config().getInteger(CONFIG_CHUHEDB_JDBC_MAX_POOL_SIZE, 30))
                .put("queryTimeout", 20000)     // 20 seconds
                .put("charset", "UTF-8")
                .put("username", "root")
                .put("password", "Cc");

        SQLClient mySQLClient  = MySQLClient.createShared(vertx.getDelegate(), mySQLClientConfig);

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

    private HashMap<ChuheSqlQuery, String> loadSqlQueries() throws IOException {

        String queriesFile = config().getString(CONFIG_CHUHEDB_SQL_QUERIES_RESOURCE_FILE);
        HashMap<ChuheSqlQuery, String> sqlQueries = new HashMap<>();

        InputStream queriesInputStream;
        if (queriesFile != null) {
            queriesInputStream = new FileInputStream(queriesFile);
        } else {
            queriesInputStream = getClass().getResourceAsStream("/properties/chuhe-db-queries.properties");
        }

        Properties queriesProps = new Properties(); queriesProps.load(queriesInputStream); queriesInputStream.close();
        sqlQueries.put(ChuheSqlQuery.CREATE_PRODUCTS_TABLE, queriesProps.getProperty("create-products-table"));
        sqlQueries.put(ChuheSqlQuery.ALL_PRODUCTS, queriesProps.getProperty("all-products"));
        sqlQueries.put(ChuheSqlQuery.GET_PRODUCT, queriesProps.getProperty("get-product"));
        sqlQueries.put(ChuheSqlQuery.CREATE_PRODUCT, queriesProps.getProperty("create-product"));
        sqlQueries.put(ChuheSqlQuery.SAVE_PRODUCT, queriesProps.getProperty("save-product"));
        sqlQueries.put(ChuheSqlQuery.DELETE_PRODUCT, queriesProps.getProperty("delete-product"));

        return  sqlQueries;
    }


}
