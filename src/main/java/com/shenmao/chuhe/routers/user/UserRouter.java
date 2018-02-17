package com.shenmao.chuhe.routers.user;

import com.shenmao.chuhe.handlers.manage.ManageCustomerHandlers;
import com.shenmao.chuhe.handlers.manage.ManageUserHandlers;
import com.shenmao.chuhe.routers.GlobalRouter;
import com.shenmao.chuhe.routers.customer.CustomerRouter;
import io.vertx.rxjava.ext.web.Router;

import static io.vertx.core.http.HttpMethod.GET;

public class UserRouter {

    private static final String _USER_ROUTE_PREFIX = "user";
    private static final String _USER_ID_ROUTE = "(?<userId>[^\\/.]+)";

    public static UserRouter create(Router router, ManageUserHandlers manageHandlers) {
        return new UserRouter(router, manageHandlers);
    }

    private Router router;
    ManageUserHandlers manageHandlers;

    public UserRouter(Router router, ManageUserHandlers manageHandlers) {
        this.router = router;
        this.manageHandlers = manageHandlers;
    }

    private static String getUserRouter(String... str) {
        return "/" + _USER_ROUTE_PREFIX + GlobalRouter.getRouter(str);
    }


    public void init() {

        this.router.routeWithRegex(GET, getUserRouter(_USER_ID_ROUTE))
                .handler(manageHandlers::userDetail);

    }

}