package com.shenmao.chuhe.routers;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.manage.ManageProductsHandlers;
import com.shenmao.chuhe.handlers.manage.ManageStoreHandlers;
import com.shenmao.chuhe.routers.product.ProductRouter;
import com.shenmao.chuhe.routers.store.StoreRouter;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.AuthHandler;

import static com.shenmao.chuhe.verticle.ChuheDbVerticle.CONFIG_CHUHEDB_QUEUE;


public class ManageRouter implements ChuheRouter {

    Vertx vertx;
    Router router;
    AuthHandler authHandler = null;
    ManageProductsHandlers manageProductsHandlers = null;
    ManageStoreHandlers manageStoreHandlers = null;
    ChuheDbService chuheDbService;

    public ManageRouter (Vertx vertx, AuthHandler authHandler) {

        this.vertx = vertx;
        this.router = Router.router(vertx);
        this.authHandler = authHandler;
        this.chuheDbService = ChuheDbService.createProxy(vertx.getDelegate(), CONFIG_CHUHEDB_QUEUE);

        this.manageProductsHandlers = ManageProductsHandlers.create(chuheDbService);
        this.manageStoreHandlers = ManageStoreHandlers.create(chuheDbService);

        this.router.route("/*").handler(authHandler);

        ProductRouter.create(this.router, this.manageProductsHandlers).init();
        StoreRouter.create(this.router, this.manageStoreHandlers).init();
    }


    @Override
    public Router getRouter() {
        return this.router;
    }

}
