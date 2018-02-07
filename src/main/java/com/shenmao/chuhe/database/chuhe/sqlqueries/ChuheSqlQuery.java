package com.shenmao.chuhe.database.chuhe.sqlqueries;

public enum ChuheSqlQuery {

    LAST_INSERT_ID,

    // products
    CREATE_PRODUCTS_TABLE,
    ALL_PRODUCTS,
    GET_PRODUCT,
    CREATE_PRODUCT,
    SAVE_PRODUCT,
    DELETE_PRODUCT,
    DELETE_PRODUCT_BATCH,


    // orders_replenish
    CREATE_ORDERS_TABLE,
    ALL_ORDERS,
    GET_ORDER,
    CREATE_ORDER,
    SAVE_ORDER,
    DELETE_ORDER,
    DELETE_ORDER_BATCH,

    // order items
    CREATE_ORDER_ITEMS_REPLENISH_TABLE,
    SAVE_ORDER_ITEMS_REPLENISH
}
