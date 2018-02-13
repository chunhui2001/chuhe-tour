package com.shenmao.chuhe.routers.order;

import com.shenmao.chuhe.handlers.manage.ManageOrderHandlers;
import com.shenmao.chuhe.routers.GlobalRouter;
import io.vertx.rxjava.ext.web.Router;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

public class OrdersRouter {

    private static final String _ORDERS_ROUTE_PREFIX = "orders";

    public static OrdersRouter create(Router router, ManageOrderHandlers manageHandlers) {
        return new OrdersRouter(router, manageHandlers);
    }

    private Router router;
    ManageOrderHandlers manageHandlers;

    public OrdersRouter(Router router, ManageOrderHandlers manageHandlers) {
        this.router = router;
        this.manageHandlers = manageHandlers;
    }


    private static String getOrdersRouter(String... str) {
        return "/" + _ORDERS_ROUTE_PREFIX + GlobalRouter.getRouter(str);
    }

    private static String getOrdersReplenishRouter(String... str) {
        return "/" + _ORDERS_ROUTE_PREFIX + "/replenish" + GlobalRouter.getRouter(str);
    }

    private static String getOrdersSalesRouter(String... str) {
        return "/" + _ORDERS_ROUTE_PREFIX + "/sales" + GlobalRouter.getRouter(str);
    }

    public void init() {

        this.router.routeWithRegex(GET, getOrdersRouter())
                .handler(manageHandlers::ordersIndex);

        this.router.routeWithRegex(GET, getOrdersReplenishRouter())
                .handler(manageHandlers::ordersReplenishIndex);

        this.router.routeWithRegex(POST, getOrdersReplenishRouter())
                .handler(routingContext -> {
                    manageHandlers.orderSave(routingContext, "replenish");
                });

        this.router.routeWithRegex(POST, getOrdersSalesRouter())
                .handler(routingContext -> {
                    manageHandlers.orderSave(routingContext, "sales");
                });

        this.router.routeWithRegex(GET, getOrdersSalesRouter())
                .handler(manageHandlers::ordersSaleIndex);

        //order details
        this.router.routeWithRegex(GET, getOrdersReplenishRouter("(?<orderId>[^\\/.]+)"))
                .handler(routingContext -> {
                    manageHandlers.orderDetail(routingContext, "replenish");
                });

        this.router.routeWithRegex(GET, getOrdersSalesRouter("(?<orderId>[^\\/.]+)"))
                .handler(routingContext -> {
                    manageHandlers.orderDetail(routingContext, "sales");
                });

    }

}
