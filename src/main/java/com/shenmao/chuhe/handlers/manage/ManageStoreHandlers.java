package com.shenmao.chuhe.handlers.manage;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.BaseHandler;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageStoreHandlers extends BaseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageStoreHandlers.class);


    public static ManageStoreHandlers create(ChuheDbService chuheDbService) {
        return new ManageStoreHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageStoreHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }



    /**
     * 所有产品列表,
     * @param routingContext
     */
    public void storeIndex(RoutingContext routingContext) {


        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/stores/stores_index.html")
                .putMessage("进销存管理");

        chainSerialization.serialize();

    }
}
