package com.shenmao.chuhe.handlers.manage;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.BaseHandler;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

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
     * 进销存管理首页
     * @param routingContext
     */
    public void storeIndex(RoutingContext routingContext) {


        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/stores/stores_index.html")
                .putMessage("进销存管理");

        chainSerialization.serialize();

    }


    /**
     * 进货管理首页
     * @param routingContext
     */
    public void storeReplenishIndex(RoutingContext routingContext) {

        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/stores/replenish/replenish_index.html")
                .putMessage("进货管理");

        chainSerialization.serialize();

    }



    private JsonObject getOrderReplenishObject(RoutingContext routingContext) {

        JsonObject result = new JsonObject();

        if (paramExists(routingContext, "order_person")) {
            result.put("order_person", getString(routingContext,"order_person"));
        }

        if (paramExists(routingContext, "order_flow_no")) {
            result.put("order_flow_no", getString(routingContext,"order_flow_no"));
        }

        if (paramExists(routingContext, "order_desc")) {
            result.put("order_desc", getString(routingContext,"order_desc"));
        }

        result.put("order_type", "replenish");
        result.put("order_money", 0.0);

        if (paramExists(routingContext, "order_date")) {
            result.put("order_date", getString(routingContext,"order_date"));
        } else {
            result.put("order_date", "2014-05-06");
        }

        result.put("user_identity", routingContext.user().principal().getString("username"));

        return result;
    }

    /**
     * 新增进货单
     * @param routingContext
     */
    public void storeReplenishSave(RoutingContext routingContext) {

        JsonObject order = getOrderReplenishObject(routingContext);

        this.chuheDbService.createOrder(order, reply -> {

            if (reply.succeeded()) {
                // Long newProductId = reply.result();
                ChainSerialization.create(routingContext.getDelegate())
                        .putContextData(reply.result())
                        .putFlashMessage("成功添加一个进货单")
                        .putMessage("成功添加一个进货单")
                        .redirect("/mans/stores/replenish");
            } else {
                ChainSerialization.create(routingContext.getDelegate())
                        // .putFlashMessage(reply.cause().getMessage())
                        .putMessage(reply.cause().getMessage())
                        .putException(reply.cause())
                        .putFlashException(reply.cause())
                        .redirect("/mans/stores/replenish");
            }

        });

    }


    /**
     * 进货管理首页
     * @param routingContext
     */
    public void storeSaleIndex(RoutingContext routingContext) {


        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/stores/sales/sales_index.html")
                .putMessage("销售管理");

        chainSerialization.serialize();

    }

}
