package com.shenmao.chuhe.routers.dealer;

import com.shenmao.chuhe.handlers.manage.ManageDealerHandlers;
import com.shenmao.chuhe.routers.GlobalRouter;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.ext.web.Router;

import static io.vertx.core.http.HttpMethod.DELETE;
import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

public class DealerRouter {

    private static final String _DEALER_ROUTE_PREFIX = "dealer";
    private static final String _DEALER_ID_ROUTE = "(?<dealerId>[^\\/.]+)";

    public static DealerRouter create(Router router, ManageDealerHandlers manageHandlers) {
        return new DealerRouter(router, manageHandlers);
    }

    private Router router;
    ManageDealerHandlers manageHandlers;

    public DealerRouter(Router router, ManageDealerHandlers manageHandlers) {
        this.router = router;
        this.manageHandlers = manageHandlers;
    }


    private static String getDealerRouter(String... str) {
        return "/" + _DEALER_ROUTE_PREFIX + GlobalRouter.getRouter(str);
    }


    public void init() {

        this.router.routeWithRegex(GET, getDealerRouter())
                .handler(manageHandlers::dealerIndex);

        this.router.routeWithRegex(POST, getDealerRouter())
                .handler(manageHandlers::createDealer);

        this.router.routeWithRegex(DELETE, getDealerRouter(_DEALER_ID_ROUTE))
                .handler(manageHandlers::deleteDealerBatch);

        this.router.routeWithRegex(DELETE, getDealerRouter())
                .handler(manageHandlers::deleteDealerBatch);

        this.router.routeWithRegex(GET, getDealerRouter("new"))
                .handler(manageHandlers::newDealer);

        this.router.routeWithRegex(GET, getDealerRouter(_DEALER_ID_ROUTE))
                .handler(manageHandlers::dealerDetail);

        this.router.routeWithRegex(GET, getDealerRouter(_DEALER_ID_ROUTE, "edit"))
                .handler(manageHandlers::dealerDetail);


        this.router.routeWithRegex(HttpMethod.PUT, getDealerRouter(_DEALER_ID_ROUTE))
                .handler(manageHandlers::dealerUpdate);


    }
}
