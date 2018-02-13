package com.shenmao.chuhe.handlers.manage;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.BaseHandler;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ManageOrderHandlers extends BaseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageOrderHandlers.class);


    public static ManageOrderHandlers create(ChuheDbService chuheDbService) {
        return new ManageOrderHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageOrderHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }



    /**
     * 进销存管理首页
     * @param routingContext
     */
    public void ordersIndex(RoutingContext routingContext) {


        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/orders/orders_index.html")
                .putMessage("进销存管理");

        chainSerialization.serialize();

    }


    /**
     * 进货管理首页
     * @param routingContext
     */
    public void ordersReplenishIndex(RoutingContext routingContext) {


        this.chuheDbService.fetchAllOrders("replenish", reply -> {

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



    private JsonObject getOrderObject(RoutingContext routingContext, String orderType) {

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

        result.put("order_type", orderType);
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
    public void orderSave(RoutingContext routingContext, String orderType) {

        final StringBuilder orderTypeName = new StringBuilder();

        switch (orderType) {
            case "replenish":
                orderTypeName.append("进货单");
                break;
            case "sales":
                orderTypeName.append("销售单");
                break;
            default:
        }

        JsonObject order = getOrderObject(routingContext, orderType);

        if (!order.containsKey("order_flow_no") || order.getString("order_flow_no").trim().isEmpty()) {
            ChainSerialization.create(routingContext.getDelegate())
                    .putFlashMessage("请提供" + orderTypeName + "流水号")
                    .redirect("/mans/orders/" + orderType);
            return;
        }

        if (!order.containsKey("order_item_count")) {
            ChainSerialization.create(routingContext.getDelegate())
                    .putFlashMessage("请提供" + orderTypeName + "条目详情数量")
                    .redirect("/mans/orders/" + orderType);
            return;
        }

        List<JsonObject> orderDetailItemList = new ArrayList<>();

        for (int i=0; i<order.getInteger("order_item_count"); i++) {

            JsonObject order_item = getJson(routingContext,"order_item_" + i);
            order_item.put("product_id", Long.parseLong(order_item.getString("product_id")));
            order_item.put("product_price", Double.parseDouble(order_item.getString("product_price")));

            if (orderType.equals("replenish")) {
                order_item.put("product_buy_count", Double.parseDouble(order_item.getString("product_buy_count")));
            } else {
                order_item.put("product_sale_count", Double.parseDouble(order_item.getString("product_sale_count")));
            }

            orderDetailItemList.add(order_item);
        }

        if (orderDetailItemList.size() == 0) {
            ChainSerialization.create(routingContext.getDelegate())
                    .putFlashMessage("不允许提交空" + orderTypeName + ", 你提交的" + orderTypeName + "必须至少包含一条商品信息!")
                    .redirect("/mans/orders/" + orderType);
            return;
        }

        this.chuheDbService.createOrder(order, orderDetailItemList, reply -> {

            if (reply.succeeded()) {
                // Long newProductId = reply.result();
                ChainSerialization.create(routingContext.getDelegate())
                        .putContextData(reply.result())
                        .putFlashMessage("成功添加一个" + orderTypeName.toString())
                        .putMessage("成功添加一个" + orderTypeName.toString())
                        .redirect("/mans/orders/" + orderType);
            } else {
                ChainSerialization.create(routingContext.getDelegate())
                        .putFlashMessage(reply.cause().getMessage())
                        .putMessage(reply.cause().getMessage())
                        .putException(reply.cause())
                        .putFlashException(reply.cause())
                        .redirect("/mans/orders/" + orderType);
            }

        });

    }

    /**
     * 进货管理首页
     * @param routingContext
     */
    public void ordersSaleIndex(RoutingContext routingContext) {


        this.chuheDbService.fetchAllOrders("sales", reply -> {

            if (reply.succeeded()) {
                ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                        .putViewName("/man/orders/sales/sales_index.html")
                        .putContextData(reply.result())
                        .putMessage("销售管理");
                chainSerialization.serialize();
                return;
            }

            ChainSerialization.create(routingContext.getDelegate())
                    .putFlashMessage(reply.cause().getMessage())
                    .putException(reply.cause())
                    .putFlashException(reply.cause())
                    .redirect("/mans/orders/sales");

        });

    }

    /**
     * 订单详情，
     * @param routingContext
     */
    public void orderDetail(RoutingContext routingContext, String orderType) {

        Long orderId = getLong(routingContext,"param0");

        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/orders/order_deatils.html")
                .putMessage("订单详情");

        this.chuheDbService.getOrderDetail(orderId, orderType, reply -> {

            if (reply.succeeded()) {

                if (reply.result().fieldNames().size() == 0) {

                    chainSerialization
                            .setStatusRealCode(404)
                            .putFlashMessage("你访问的订单不存在")
                            .putContextData(null)
                            .redirect("/not-found", true);
                    return;
                }

                chainSerialization.putContextData(reply.result());
            } else {
                chainSerialization
                        .putFlashMessage(reply.cause().getMessage())
                        .putMessage(reply.cause().getMessage())
                        .putException(reply.cause())
                        .putFlashException(reply.cause());
            }

            chainSerialization.serialize();

        });

    }

}
