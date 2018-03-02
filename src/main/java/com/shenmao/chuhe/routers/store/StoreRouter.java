package com.shenmao.chuhe.routers.store;

import com.shenmao.chuhe.handlers.StoreHandlers;
import com.shenmao.chuhe.routers.GlobalRouter;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.rxjava.ext.web.Router;

import static io.vertx.core.http.HttpMethod.GET;

public class StoreRouter {

    public static StoreRouter create(Router router, StoreHandlers storeHandlers) {
        return new StoreRouter(router, storeHandlers);
    }

    private Router router;
    private StoreHandlers storeHandlers = null;

    public StoreRouter(Router router, StoreHandlers storeHandlers) {
        this.router = router;
        this.storeHandlers = storeHandlers;
    }

    public void init() {

        this.router.routeWithRegex(GET, "/store/p/(?<pid>[^\\/.]+)")
                .handler(storeHandlers::productDetailHandler);

        this.router.routeWithRegex(GET, "/store/(?<storeIdOrName>[^\\/.]+)")
                .handler(storeHandlers::indexHandler);
    }


}
