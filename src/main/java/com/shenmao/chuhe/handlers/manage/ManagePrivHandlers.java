package com.shenmao.chuhe.handlers.manage;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.BaseHandler;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagePrivHandlers extends BaseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageDealerHandlers.class);


    public static ManagePrivHandlers create(ChuheDbService chuheDbService) {
        return new ManagePrivHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManagePrivHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }

    public void privIndex(RoutingContext routingContext) {

        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate());

        this.chuheDbService.fetchAllUsers(reply -> {

            if (reply.succeeded()) {

                routingContext.put("all_user_roles", getUserRoles().encode());

                chainSerialization
                        .putContextData(reply.result())
                        .putViewName("/man/priv/priv_index.html")
                        .putMessage("应用权限管理");

            } else {
                chainSerialization
                        .putFlashMessage(reply.cause().getMessage())
                        .putMessage(reply.cause().getMessage())
                        .putException(reply.cause())
                        .putFlashException(reply.cause());
            }

            chainSerialization.serialize();

        });

    }

    private JsonObject getUserRoles() {
        JsonObject userRoles = new JsonObject();
        userRoles.put("role_user", "普通用户");
        userRoles.put("role_customer", "客户");
        userRoles.put("role_dealer", "经销商");
        //userRoles.put("role_sys_admin", "系统管理员");
        //userRoles.put("role_super_admin", "超级管理员");
        userRoles.put("role_admin", "管理员");
        return userRoles;
    }

    public void userRoles(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("Content-Type", "application/json;charset=UTF-8")
                .end(getUserRoles().encode());
    }

    public void grantRoles(RoutingContext routingContext) {

        Long userId = getLong(routingContext, "user_id");
        String userRoles = getString(routingContext, "user_roles");

        this.chuheDbService.updateUserRoles(userId, userRoles, reply -> {
            routingContext.response().end(reply.result() + "");
        });

    }

}
