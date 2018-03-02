package com.shenmao.chuhe.handlers;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.database.wikipage.WikiPageDbService;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.rxjava.ext.web.RoutingContext;

public class StoreHandlers {

    ChuheDbService chuheDbService;

    public static StoreHandlers create(ChuheDbService chuheDbService) {
        return new StoreHandlers(chuheDbService);
    }


    public StoreHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }

    public void indexHandler(RoutingContext routingContext) {

        ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/store/store_index.html")
                .putContextData(null)
                .serialize();
    }


    public void productDetailHandler(RoutingContext routingContext) {

        ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/store/product_detail.html")
                .putContextData(null)
                .serialize();
    }

}
