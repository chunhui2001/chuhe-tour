package com.shenmao.chuhe.routers.order;

import com.shenmao.chuhe.handlers.manage.ManageStoreHandlers;
import com.shenmao.chuhe.routers.GlobalRouter;
import io.vertx.rxjava.ext.web.Router;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

public class OrdersRouter {

    private static final String _STORE_ROUTE_PREFIX = "orders";

    public static OrdersRouter create(Router router, ManageStoreHandlers manageHandlers) {
        return new OrdersRouter(router, manageHandlers);
    }

    private Router router;
    ManageStoreHandlers manageHandlers;

    public OrdersRouter(Router router, ManageStoreHandlers manageHandlers) {
        this.router = router;
        this.manageHandlers = manageHandlers;
    }


    private static String getStoreRouter(String... str) {
        return "/" + _STORE_ROUTE_PREFIX + GlobalRouter.getRouter(str);
    }

    private static String getStoreReplenishRouter(String... str) {
        return "/" + _STORE_ROUTE_PREFIX + "/replenish" + GlobalRouter.getRouter(str);
    }

    private static String getOrdersSalesRouter(String... str) {
        return "/" + _STORE_ROUTE_PREFIX + "/sales" + GlobalRouter.getRouter(str);
    }

    public void init() {

        this.router.routeWithRegex(GET, getStoreRouter())
                .handler(manageHandlers::storeIndex);

        this.router.routeWithRegex(GET, getStoreReplenishRouter())
                .handler(manageHandlers::storeReplenishIndex);

        this.router.routeWithRegex(POST, getStoreReplenishRouter())
                .handler(manageHandlers::storeReplenishSave);

        this.router.routeWithRegex(GET, getOrdersSalesRouter())
                .handler(manageHandlers::ordersSaleIndex);
    }

}
