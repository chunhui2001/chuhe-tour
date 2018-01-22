package com.shenmao.chuhe.handlers;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;

public class ManageHandlers {


    public static ManageHandlers create(ChuheDbService chuheDbService) {

        return new ManageHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }
    /**
     * 所有商品列表,
     * @param routingContext
     */
    public void products(RoutingContext routingContext) {


        chuheDbService.fetchAllProducts(reply -> {

            System.out.println(reply.succeeded() + ", ManageHandlers products");
        });

        JsonObject contextData = new JsonObject();


        ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/products/products_index.html")
                .putContextData(contextData)
                .serialize();
    }

}
