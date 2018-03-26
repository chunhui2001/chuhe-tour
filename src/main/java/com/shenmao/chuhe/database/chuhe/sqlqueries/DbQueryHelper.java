package com.shenmao.chuhe.database.chuhe.sqlqueries;

import com.shenmao.chuhe.commons.PropertyParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

public class DbQueryHelper {

    public static HashMap<ChuheSqlQuery, String> sqlQueries = new HashMap<>();

    static {

        try {
            putUsersSqlQueries(sqlQueries);
            putProductSqlQueries(sqlQueries);
            putOrderSqlQueries(sqlQueries);
            putStockSqlQueries(sqlQueries);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static HashMap<ChuheSqlQuery, String> getSqlQueries() {
        return sqlQueries;
    }

    private static void putUsersSqlQueries(HashMap<ChuheSqlQuery, String> sqlQueries) throws IOException {

        InputStream queriesInputStream = DbQueryHelper.class.getResourceAsStream("/properties/database-sql-queries/chuhe-db-users-queries.properties");

        Properties queriesProps = new PropertyParser();
        queriesProps.load(new InputStreamReader(queriesInputStream, "utf-8"));
        queriesInputStream.close();

        sqlQueries.put(ChuheSqlQuery.CREATE_USERS_TABLE, queriesProps.getProperty("create-users-table"));
        sqlQueries.put(ChuheSqlQuery.SAVE_USER, queriesProps.getProperty("save-user"));
        sqlQueries.put(ChuheSqlQuery.DELETE_USER_BATCH, queriesProps.getProperty("delete-user-batch"));
        sqlQueries.put(ChuheSqlQuery.ALL_USERS, queriesProps.getProperty("all-users"));
        sqlQueries.put(ChuheSqlQuery.GET_USER, queriesProps.getProperty("get-user"));
        sqlQueries.put(ChuheSqlQuery.VALIDATE_USER, queriesProps.getProperty("validate-user"));
        sqlQueries.put(ChuheSqlQuery.GET_USER_ROLES, queriesProps.getProperty("get-user-roles"));
        sqlQueries.put(ChuheSqlQuery.UPDATE_USER_ROLES, queriesProps.getProperty("update-user-roles"));

        sqlQueries.put(ChuheSqlQuery.CREATE_DEALER_TABLE, queriesProps.getProperty("create-dealer-table"));
        sqlQueries.put(ChuheSqlQuery.SAVE_DEALER, queriesProps.getProperty("save-dealer"));
        sqlQueries.put(ChuheSqlQuery.ALL_DEALERS, queriesProps.getProperty("all-dealer"));
        sqlQueries.put(ChuheSqlQuery.DELETE_DEALER_BATCH, queriesProps.getProperty("delete-dealer-batch"));
        sqlQueries.put(ChuheSqlQuery.GET_DEALER, queriesProps.getProperty("get-dealer"));

        sqlQueries.put(ChuheSqlQuery.UPDATE_USER, queriesProps.getProperty("update-user"));
        sqlQueries.put(ChuheSqlQuery.UPDATE_DEALER, queriesProps.getProperty("update-dealer"));

    }


    private static void putProductSqlQueries(HashMap<ChuheSqlQuery, String> sqlQueries) throws IOException {

        InputStream queriesInputStream = DbQueryHelper.class.getResourceAsStream("/properties/database-sql-queries/chuhe-db-products-queries.properties");

        Properties queriesProps = new PropertyParser();
        queriesProps.load(new InputStreamReader(queriesInputStream, "utf-8"));
        queriesInputStream.close();

        sqlQueries.put(ChuheSqlQuery.CREATE_PRODUCTS_TABLE, queriesProps.getProperty("create-products-table"));
        sqlQueries.put(ChuheSqlQuery.ALL_PRODUCTS, queriesProps.getProperty("all-products"));
        sqlQueries.put(ChuheSqlQuery.GET_PRODUCT, queriesProps.getProperty("get-product"));
        sqlQueries.put(ChuheSqlQuery.CREATE_PRODUCT, queriesProps.getProperty("create-product"));
        sqlQueries.put(ChuheSqlQuery.SAVE_PRODUCT, queriesProps.getProperty("save-product"));
        sqlQueries.put(ChuheSqlQuery.DELETE_PRODUCT, queriesProps.getProperty("delete-product"));
        sqlQueries.put(ChuheSqlQuery.DELETE_PRODUCT_BATCH, queriesProps.getProperty("delete-product-batch"));
        sqlQueries.put(ChuheSqlQuery.LAST_INSERT_ID, queriesProps.getProperty("last_insert_id"));
        sqlQueries.put(ChuheSqlQuery.GET_PRODUCTS_BY_IDLIST, queriesProps.getProperty("get-products-by-id-list"));

        sqlQueries.put(ChuheSqlQuery.FILTER_PRODUCTS_BY_NAME, queriesProps.getProperty("filter-products-by-name"));

    }


    private static void putOrderSqlQueries(HashMap<ChuheSqlQuery, String> sqlQueries) throws IOException {

        InputStream queriesInputStream = DbQueryHelper.class.getResourceAsStream("/properties/database-sql-queries/chuhe-db-orders-queries.properties");

        Properties queriesProps = new PropertyParser();
        queriesProps.load(new InputStreamReader(queriesInputStream, "utf-8"));
        queriesInputStream.close();

        sqlQueries.put(ChuheSqlQuery.CREATE_ORDERS_TABLE, queriesProps.getProperty("create-orders-table"));
        sqlQueries.put(ChuheSqlQuery.ALL_ORDERS, queriesProps.getProperty("all-orders"));
        sqlQueries.put(ChuheSqlQuery.GET_ORDER, queriesProps.getProperty("get-order"));
        sqlQueries.put(ChuheSqlQuery.CREATE_ORDER, queriesProps.getProperty("create-order"));
        sqlQueries.put(ChuheSqlQuery.SAVE_ORDER, queriesProps.getProperty("save-order"));
        sqlQueries.put(ChuheSqlQuery.DELETE_ORDER, queriesProps.getProperty("delete-order"));
        sqlQueries.put(ChuheSqlQuery.DELETE_ORDER_BATCH, queriesProps.getProperty("delete-order-batch"));

        sqlQueries.put(ChuheSqlQuery.CREATE_ORDER_ITEMS_REPLENISH_TABLE, queriesProps.getProperty("create-order-items-replenish-table"));
        sqlQueries.put(ChuheSqlQuery.SAVE_ORDER_ITEMS_REPLENISH, queriesProps.getProperty("save-order-items-replenish"));
        sqlQueries.put(ChuheSqlQuery.GET_ORDER_ITEMS_REPLENISH, queriesProps.getProperty("get-order-items-replenish"));

        sqlQueries.put(ChuheSqlQuery.CREATE_ORDER_ITEMS_SALES_TABLE, queriesProps.getProperty("create-order-items-sales-table"));
        sqlQueries.put(ChuheSqlQuery.SAVE_ORDER_ITEMS_SALES, queriesProps.getProperty("save-order-items-sales"));
        sqlQueries.put(ChuheSqlQuery.GET_ORDER_ITEMS_SALES, queriesProps.getProperty("get-order-items-sales"));

    }

    private static void putStockSqlQueries(HashMap<ChuheSqlQuery, String> sqlQueries) throws IOException {

        InputStream queriesInputStream = DbQueryHelper.class.getResourceAsStream("/properties/database-sql-queries/chuhe-db-stock-queries.properties");

        Properties queriesProps = new PropertyParser();
        queriesProps.load(new InputStreamReader(queriesInputStream, "utf-8"));
        queriesInputStream.close();

        sqlQueries.put(ChuheSqlQuery.CREATE_STOCK_TABLE, queriesProps.getProperty("create-stock-table"));
        sqlQueries.put(ChuheSqlQuery.SAVE_STOCK, queriesProps.getProperty("save-stock"));
        sqlQueries.put(ChuheSqlQuery.GET_ALL_STOCK, queriesProps.getProperty("all-stocks"));
        sqlQueries.put(ChuheSqlQuery.PRODUCT_STOCK, queriesProps.getProperty("product-stocks"));

    }

}
