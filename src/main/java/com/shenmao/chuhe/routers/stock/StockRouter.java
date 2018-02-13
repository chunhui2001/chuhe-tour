package com.shenmao.chuhe.routers.stock;

import com.shenmao.chuhe.handlers.manage.ManageOrderHandlers;
import com.shenmao.chuhe.handlers.manage.ManageStockHandlers;
import com.shenmao.chuhe.routers.GlobalRouter;
import com.shenmao.chuhe.routers.order.OrdersRouter;
import io.vertx.rxjava.ext.web.Router;

import static io.vertx.core.http.HttpMethod.GET;

public class StockRouter {

    private static final String _STOCK_ROUTE_PREFIX = "stock";

    public static StockRouter create(Router router, ManageStockHandlers manageHandlers) {
        return new StockRouter(router, manageHandlers);
    }

    private Router router;
    ManageStockHandlers manageHandlers;

    public StockRouter(Router router, ManageStockHandlers manageHandlers) {
        this.router = router;
        this.manageHandlers = manageHandlers;
    }


    private static String getStockRouter(String... str) {
        return "/" + _STOCK_ROUTE_PREFIX + GlobalRouter.getRouter(str);
    }

    public void init() {
        this.router.routeWithRegex(GET, getStockRouter())
                .handler(manageHandlers::stockIndex);
    }

}
