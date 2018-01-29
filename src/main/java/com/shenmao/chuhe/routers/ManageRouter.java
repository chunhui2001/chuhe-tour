package com.shenmao.chuhe.routers;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.GlobalHandlers;
import com.shenmao.chuhe.handlers.manage.ManageProductsHandlers;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.AuthHandler;

import static com.shenmao.chuhe.verticle.ChuheDbVerticle.CONFIG_CHUHEDB_QUEUE;
import static io.vertx.core.http.HttpMethod.DELETE;
import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;


public class ManageRouter implements ChuheRouter {

    Vertx vertx;
    Router router;
    AuthHandler authHandler = null;
    ManageProductsHandlers manageHandlers = null;
    ChuheDbService chuheDbService;

    public ManageRouter (Vertx vertx, AuthHandler authHandler) {

        this.vertx = vertx;
        this.router = Router.router(vertx);
        this.authHandler = authHandler;
        this.chuheDbService = ChuheDbService.createProxy(vertx.getDelegate(), CONFIG_CHUHEDB_QUEUE);
        this.manageHandlers = ManageProductsHandlers.create(chuheDbService);

        this.router.route("/*").handler(authHandler);
        this.productRouter();
    }


    private void productRouter() {

        this.router
                .routeWithRegex(GET, "/products" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
                .handler(manageHandlers::productsList);

        this.router
                .routeWithRegex(POST, "/products" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
                .handler(manageHandlers::productsSave);

        this.router
                .routeWithRegex(GET, "/products/(?<productId>[^\\/.]+)" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
                .handler(manageHandlers::productDetail);

        this.router
                .routeWithRegex(DELETE, "/products/(?<productId>[^\\/.]+)" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
                .handler(manageHandlers::productDelete);

        this.router
                .routeWithRegex(HttpMethod.PUT, "/products/(?<productId>[^\\/.]+)" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
                .handler(manageHandlers::productUpdate);

        this.router
                .routeWithRegex(GET, "/products/(?<productId>[^\\/.]+)/edit" + GlobalHandlers._SUPPORT_EXTS_PATTERN)
                .handler(manageHandlers::productDetail);

    }


    @Override
    public Router getRouter() {
        return this.router;
    }
}
