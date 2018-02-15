package com.shenmao.chuhe.routers;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.manage.*;
import com.shenmao.chuhe.routers.customer.CustomerRouter;
import com.shenmao.chuhe.routers.dealer.DealerRouter;
import com.shenmao.chuhe.routers.product.ProductRouter;
import com.shenmao.chuhe.routers.order.OrdersRouter;
import com.shenmao.chuhe.routers.stock.StockRouter;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.AuthHandler;

import static com.shenmao.chuhe.verticle.ChuheDbVerticle.CONFIG_CHUHEDB_QUEUE;


public class ManageRouter implements ChuheRouter {

    Vertx vertx;
    Router router;
    AuthHandler authHandler = null;
    ManageProductsHandlers manageProductsHandlers = null;
    ManageOrderHandlers manageStoreHandlers = null;
    ManageStockHandlers manageStockHandlers = null;
    ManageDealerHandlers manageDealerHandlers = null;
    ManageCustomerHandlers manageCustomerHandlers = null;

    ChuheDbService chuheDbService;

    public ManageRouter (Vertx vertx, AuthHandler authHandler) {

        this.vertx = vertx;
        this.router = Router.router(vertx);
        this.authHandler = authHandler;
        this.chuheDbService = ChuheDbService.createProxy(vertx.getDelegate(), CONFIG_CHUHEDB_QUEUE);

        this.manageProductsHandlers = ManageProductsHandlers.create(chuheDbService);
        this.manageStoreHandlers = ManageOrderHandlers.create(chuheDbService);
        this.manageStockHandlers = ManageStockHandlers.create(chuheDbService);
        this.manageDealerHandlers = ManageDealerHandlers.create(chuheDbService);
        this.manageCustomerHandlers = ManageCustomerHandlers.create(chuheDbService);

        this.router.route("/*").handler(authHandler);

        ProductRouter.create(this.router, this.manageProductsHandlers).init();
        OrdersRouter.create(this.router, this.manageStoreHandlers).init();
        StockRouter.create(this.router, this.manageStockHandlers).init();
        DealerRouter.create(this.router, this.manageDealerHandlers).init();
        CustomerRouter.create(this.router, this.manageCustomerHandlers).init();
    }


    @Override
    public Router getRouter() {
        return this.router;
    }

}
