package com.shenmao.chuhe.routers.store;

import com.shenmao.chuhe.handlers.manage.ManageStoreHandlers;
import com.shenmao.chuhe.routers.GlobalRouter;
import io.vertx.rxjava.ext.web.Router;

import static io.vertx.core.http.HttpMethod.GET;

public class StoreRouter {

    private static final String _STORE_ROUTE_PREFIX = "stores";

    public static StoreRouter create(Router router, ManageStoreHandlers manageHandlers) {
        return new StoreRouter(router, manageHandlers);
    }

    private Router router;
    ManageStoreHandlers manageHandlers;

    public StoreRouter(Router router, ManageStoreHandlers manageHandlers) {
        this.router = router;
        this.manageHandlers = manageHandlers;
    }


    private static String getStoreRouter(String... str) {
        return "/" + _STORE_ROUTE_PREFIX + GlobalRouter.getRouter(str);
    }

    public void init() {

        this.router.routeWithRegex(GET, getStoreRouter())
                .handler(manageHandlers::storeIndex);
    }

}
