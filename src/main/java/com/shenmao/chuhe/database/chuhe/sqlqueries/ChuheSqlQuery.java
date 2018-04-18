package com.shenmao.chuhe.database.chuhe.sqlqueries;

public enum ChuheSqlQuery {

    LAST_INSERT_ID,

    // users
    CREATE_USERS_TABLE,
    SAVE_USER,
    DELETE_USER_BATCH,
    UPDATE_USER,
    ALL_USERS,
    GET_USER,
    GET_USER_ROLES,
    UPDATE_USER_ROLES,
    VALIDATE_USER,

    // products
    CREATE_PRODUCTS_TABLE,
    ALL_PRODUCTS,
    GET_PRODUCT,
    CREATE_PRODUCT,
    SAVE_PRODUCT,
    DELETE_PRODUCT,
    DELETE_PRODUCT_BATCH,
    GET_PRODUCTS_BY_IDLIST,
    FILTER_PRODUCTS_BY_NAME,

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
    GET_ORDER_ITEMS_REPLENISH,

    // order sales items
    CREATE_ORDER_ITEMS_SALES_TABLE,
    SAVE_ORDER_ITEMS_SALES,
    GET_ORDER_ITEMS_SALES,

    // stock
    CREATE_STOCK_TABLE,
    SAVE_STOCK,
    GET_ALL_STOCK,
    PRODUCT_STOCK,

    // dealer
    CREATE_DEALER_TABLE,
    SAVE_DEALER,
    ALL_DEALERS,
    DELETE_DEALER_BATCH,
    GET_DEALER,
    UPDATE_DEALER,

    // check_code
    CREATE_CHECKCODE_TABLE,
    SAVE_CHECKCODE,
    CONFIRM_CHECKCODE,
    VALIDATE_CHECKCODE,
    UPDATE_CONFIRM_TIME

}
