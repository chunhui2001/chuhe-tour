package com.shenmao.chuhe.handlers.manage;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.BaseHandler;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageCustomerHandlers extends BaseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageDealerHandlers.class);


    public static ManageCustomerHandlers create(ChuheDbService chuheDbService) {
        return new ManageCustomerHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageCustomerHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }


    public void customerIndex(RoutingContext routingContext) {

        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate())
                        .putViewName("/man/customer/customer_index.html")
                        .putMessage("客户管理 ");

        chainSerialization.serialize();
    }

}
