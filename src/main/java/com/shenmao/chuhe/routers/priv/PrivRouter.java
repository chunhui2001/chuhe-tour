package com.shenmao.chuhe.routers.priv;

import com.shenmao.chuhe.handlers.manage.ManageCustomerHandlers;
import com.shenmao.chuhe.handlers.manage.ManagePrivHandlers;
import com.shenmao.chuhe.routers.GlobalRouter;
import com.shenmao.chuhe.routers.customer.CustomerRouter;
import io.vertx.rxjava.ext.web.Router;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

public class PrivRouter {

    private static final String _PRIV_ROUTE_PREFIX = "priv";
    //private static final String _PRIV_ID_ROUTE = "(?<customerId>[^\\/.]+)";

    public static PrivRouter create(Router router, ManagePrivHandlers manageHandlers) {
        return new PrivRouter(router, manageHandlers);
    }

    private Router router;
    ManagePrivHandlers manageHandlers;

    public PrivRouter(Router router, ManagePrivHandlers manageHandlers) {
        this.router = router;
        this.manageHandlers = manageHandlers;
    }


    private static String getPrivRouter(String... str) {
        return "/" + _PRIV_ROUTE_PREFIX + GlobalRouter.getRouter(str);
    }


    public void init() {

        this.router.routeWithRegex(GET, getPrivRouter())
                .handler(manageHandlers::privIndex);

        this.router.routeWithRegex(GET, getPrivRouter("roles"))
                .handler(manageHandlers::userRoles);

        this.router.routeWithRegex(POST, getPrivRouter())
                .handler(manageHandlers::grantRoles);

    }

}
