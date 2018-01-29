package com.shenmao.chuhe.routers.product;

import com.shenmao.chuhe.handlers.manage.ManageProductsHandlers;
import com.shenmao.chuhe.routers.GlobalRouter;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.ext.web.Router;

import static io.vertx.core.http.HttpMethod.DELETE;
import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;

public class ProductRouter {

    private static final String _PRODUCT_ROUTE_PREFIX = "products";
    private static final String _PRODUCT_ID_ROUTE = "(?<productId>[^\\/.]+)";

    private static String getProductRouter(String... str) {
        return "/" + _PRODUCT_ROUTE_PREFIX + GlobalRouter.getRouter(str);
    }


    public static ProductRouter create(Router router, ManageProductsHandlers manageHandlers) {
        return new ProductRouter(router, manageHandlers);
    }

    private Router router;
    ManageProductsHandlers manageHandlers;

    public ProductRouter(Router router, ManageProductsHandlers manageHandlers) {
        this.router = router;
        this.manageHandlers = manageHandlers;
    }


    public void init() {

        this.router.routeWithRegex(GET, getProductRouter())
                .handler(manageHandlers::productsList);

        this.router.routeWithRegex(POST, getProductRouter())
                .handler(manageHandlers::productsSave);

        this.router.routeWithRegex(GET, getProductRouter(_PRODUCT_ID_ROUTE))
                .handler(manageHandlers::productDetail);

        this.router.routeWithRegex(DELETE, getProductRouter(_PRODUCT_ID_ROUTE))
                .handler(manageHandlers::productDelete);

        this.router.routeWithRegex(HttpMethod.PUT, getProductRouter(_PRODUCT_ID_ROUTE))
                .handler(manageHandlers::productUpdate);

        this.router.routeWithRegex(GET, getProductRouter(_PRODUCT_ID_ROUTE, "edit"))
                .handler(manageHandlers::productDetail);

    }


}
