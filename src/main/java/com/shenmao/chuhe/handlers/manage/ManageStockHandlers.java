package com.shenmao.chuhe.handlers.manage;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageStockHandlers {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageStockHandlers.class);


    public static ManageStockHandlers create(ChuheDbService chuheDbService) {
        return new ManageStockHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageStockHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }


    public void stockIndex(RoutingContext routingContext) {

        this.chuheDbService.fetchAllStocks(listAsyncResult -> {

            ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                    .putViewName("/man/stock/stock_index.html")
                    .putContextData(listAsyncResult.result())
                    .putMessage("库存管理");

            chainSerialization.serialize();
        });


    }
}
