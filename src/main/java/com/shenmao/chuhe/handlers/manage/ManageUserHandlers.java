package com.shenmao.chuhe.handlers.manage;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.BaseHandler;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageUserHandlers extends BaseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageDealerHandlers.class);


    public static ManageUserHandlers create(ChuheDbService chuheDbService) {
        return new ManageUserHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageUserHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }


    public void userDetail(RoutingContext routingContext) {

        Long userId = getLong(routingContext,"param0");

        String requestPath = routingContext.normalisedPath() ;

        if (routingContext.request().query() != null) {
            requestPath = requestPath + "?" + routingContext.request().query();
        }

        routingContext.reroute(requestPath.replace("/user/", "/customer/"));

    }

}
