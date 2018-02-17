package com.shenmao.chuhe.routers.customer;

import com.shenmao.chuhe.handlers.manage.ManageCustomerHandlers;
import com.shenmao.chuhe.handlers.manage.ManageDealerHandlers;
import com.shenmao.chuhe.routers.GlobalRouter;
import com.shenmao.chuhe.routers.dealer.DealerRouter;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.ext.web.Router;

import static io.vertx.core.http.HttpMethod.DELETE;
import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

public class CustomerRouter {

    private static final String _CUSTOMER_ROUTE_PREFIX = "customer";
    private static final String _CUSTOMER_ID_ROUTE = "(?<customerId>[^\\/.]+)";

    public static CustomerRouter create(Router router, ManageCustomerHandlers manageHandlers) {
        return new CustomerRouter(router, manageHandlers);
    }

    private Router router;
    ManageCustomerHandlers manageHandlers;

    public CustomerRouter(Router router, ManageCustomerHandlers manageHandlers) {
        this.router = router;
        this.manageHandlers = manageHandlers;
    }


    private static String getCustomerRouter(String... str) {
        return "/" + _CUSTOMER_ROUTE_PREFIX + GlobalRouter.getRouter(str);
    }


    public void init() {

        this.router.routeWithRegex(GET, getCustomerRouter())
                .handler(manageHandlers::customerIndex);

        this.router.routeWithRegex(GET, getCustomerRouter("new"))
                .handler(manageHandlers::newCustomer);

        this.router.routeWithRegex(POST, getCustomerRouter())
                .handler(manageHandlers::createCustomer);

        this.router.routeWithRegex(DELETE, getCustomerRouter(_CUSTOMER_ID_ROUTE))
                .handler(manageHandlers::deleteCustomerBatch);

        this.router.routeWithRegex(DELETE, getCustomerRouter())
                .handler(manageHandlers::deleteCustomerBatch);

        this.router.routeWithRegex(GET, getCustomerRouter(_CUSTOMER_ID_ROUTE))
                .handler(manageHandlers::customerDetail);

        this.router.routeWithRegex(HttpMethod.PUT, getCustomerRouter(_CUSTOMER_ID_ROUTE))
                .handler(manageHandlers::customerUpdate);

        this.router.routeWithRegex(GET, getCustomerRouter(_CUSTOMER_ID_ROUTE, "edit"))
                .handler(manageHandlers::customerDetail);


    }

}
