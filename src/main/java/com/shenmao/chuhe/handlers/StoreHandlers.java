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


        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/store/product_detail.html");

        Long productId = Long.parseLong(routingContext.request().getParam("param0"));
//        this.chuheDbService.fetchProductById()

        this.chuheDbService.fetchProductById(productId, reply -> {

            if (!reply.succeeded()) {
                chainSerialization
                        .putMessage("未能取得产品详情")
                        .putException(reply.cause()).serialize();
                return;
            }

            if (reply.result().fieldNames().size() == 0) {
                chainSerialization
                        .setStatusRealCode(404)
                        .putFlashMessage("你访问的产品不存在")
                        .putContextData(null)
                        .redirect("/not-found", true);
                return;
            }

            chainSerialization
                    .putContextData(null)
                    .serialize();

        });


    }

}
