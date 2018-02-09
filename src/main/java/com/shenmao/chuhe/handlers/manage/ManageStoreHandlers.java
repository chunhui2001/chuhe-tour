package com.shenmao.chuhe.handlers.manage;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.BaseHandler;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                .putViewName("/man/orders/orders_index.html")
                .putMessage("进销存管理");

        chainSerialization.serialize();

    }


    /**
     * 进货管理首页
     * @param routingContext
     */
    public void storeReplenishIndex(RoutingContext routingContext) {


        this.chuheDbService.fetchAllOrders(reply -> {

            if (reply.succeeded()) {
                ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                        .putViewName("/man/orders/replenish/replenish_index.html")
                        .putContextData(reply.result())
                        .putMessage("进货管理");
                chainSerialization.serialize();
                return;
            }


            ChainSerialization.create(routingContext.getDelegate())
                    .putFlashMessage(reply.cause().getMessage())
                    .putException(reply.cause())
                    .putFlashException(reply.cause())
                    .redirect("/mans/orders/replenish");

        });



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
        }

        if (paramExists(routingContext, "order_item_count")) {
            result.put("order_item_count", getInteger(routingContext,"order_item_count"));
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

        if (!order.containsKey("order_flow_no") || order.getString("order_flow_no").trim().isEmpty()) {
            ChainSerialization.create(routingContext.getDelegate())
                    .putFlashMessage("请提供订单流~水号")
                    .redirect("/mans/orders/replenish");
            return;
        }

        if (!order.containsKey("order_item_count")) {
            ChainSerialization.create(routingContext.getDelegate())
                    .putFlashMessage("请提供订单条目详情数量")
                    .redirect("/mans/orders/replenish");
            return;
        }

        List<JsonObject> orderDetailItemList = new ArrayList<>();

        for (int i=0; i<order.getInteger("order_item_count"); i++) {
            JsonObject order_item = getJson(routingContext,"order_item_" + i);
            order_item.put("product_id", Long.parseLong(order_item.getString("product_id")));
            order_item.put("product_price", Double.parseDouble(order_item.getString("product_price")));
            order_item.put("product_buy_count", Double.parseDouble(order_item.getString("product_buy_count")));
            orderDetailItemList.add(order_item);
        }

        if (orderDetailItemList.size() == 0) {
            ChainSerialization.create(routingContext.getDelegate())
                    .putFlashMessage("不允许提交空订单, 你提交的订单必须至少包含一条商品信息!")
                    .redirect("/mans/orders/replenish");
            return;
        }

        this.chuheDbService.createOrder(order, orderDetailItemList, reply -> {

            if (reply.succeeded()) {
                // Long newProductId = reply.result();
                ChainSerialization.create(routingContext.getDelegate())
                        .putContextData(reply.result())
                        .putFlashMessage("成功添加一个进货单")
                        .putMessage("成功添加一个进货单")
                        .redirect("/mans/orders/replenish");
            } else {
                ChainSerialization.create(routingContext.getDelegate())
                        .putFlashMessage(reply.cause().getMessage())
                        .putMessage(reply.cause().getMessage())
                        .putException(reply.cause())
                        .putFlashException(reply.cause())
                        .redirect("/mans/orders/replenish");
            }

        });

    }


    /**
     * 进货管理首页
     * @param routingContext
     */
    public void ordersSaleIndex(RoutingContext routingContext) {


        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/orders/sales/sales_index.html")
                .putMessage("销售管理");

        chainSerialization.serialize();

    }

}
