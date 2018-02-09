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
    GET_PRODUCTS_BY_IDLIST,


    // orders
    CREATE_ORDERS_TABLE,
    ALL_ORDERS,
    GET_ORDER,
    CREATE_ORDER,
    SAVE_ORDER,
    DELETE_ORDER,
    DELETE_ORDER_BATCH,

    // order replenish items
    CREATE_ORDER_ITEMS_REPLENISH_TABLE,
    SAVE_ORDER_ITEMS_REPLENISH,
    GET_ORDER_ITEMS_REPLENISH
}
