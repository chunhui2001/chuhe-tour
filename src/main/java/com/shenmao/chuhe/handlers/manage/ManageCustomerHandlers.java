package com.shenmao.chuhe.handlers.manage;

import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.BaseHandler;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ManageCustomerHandlers extends BaseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageDealerHandlers.class);


    public static ManageCustomerHandlers create(ChuheDbService chuheDbService) {
        return new ManageCustomerHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageCustomerHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }

    // 客户即用户
    public void customerIndex(RoutingContext routingContext) {


        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate());

        this.chuheDbService.fetchAllUsers(reply -> {

            if (reply.succeeded()) {

                chainSerialization
                        .putContextData(reply.result())
                        .putViewName("/man/customer/customer_index.html")
                        .putMessage("客户管理");

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

    public void newCustomer(RoutingContext routingContext) {

        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate());

        chainSerialization.putContextData(new JsonObject())
                .putViewName("/man/customer/customer_new.html")
                .putMessage("新增一个客户");

        chainSerialization.serialize();
    }



    public JsonObject getCustomerObject(RoutingContext routingContext) {

        JsonObject dealer = new JsonObject();

        dealer.put("user_name", getString(routingContext, "user_name"));
        dealer.put("user_gender", getString(routingContext, "user_gender"));
        dealer.put("user_identity", getString(routingContext, "user_identity"));
        dealer.put("user_source_from", getString(routingContext, "user_source_from"));
        dealer.put("user_phone", getString(routingContext, "user_phone"));
        dealer.put("user_home_tel", getString(routingContext, "user_home_tel"));
        dealer.put("home_address", getString(routingContext, "home_address"));
        dealer.put("wchat_id", getString(routingContext, "wchat_id"));

        return dealer;

    }

    public void createCustomer(RoutingContext routingContext) {


        JsonObject customer = getCustomerObject(routingContext);

        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate());

        JsonArray userRoles = new JsonArray();

        userRoles.add("role_user");
        userRoles.add("role_customer");

        this.chuheDbService.createUser(customer, userRoles, reply -> {

            if (reply.succeeded()) {
                chainSerialization
                        .putContextData(reply.result())
                        .putFlashMessage("成功添加一个客户")
                        .putMessage("成功添加一个客户");

                chainSerialization.redirect("/mans/customer");
            } else {
                chainSerialization
                        .putFlashMessage(reply.cause().getMessage())
                        .putMessage(reply.cause().getMessage())
                        .putException(reply.cause())
                        .putFlashException(reply.cause());

                chainSerialization.redirect("/mans/customer/new");
            }


        });

    }

    public void deleteCustomerBatch(RoutingContext routingContext) {

        String user_ids = getString(routingContext, "user_ids");
        String userId = getString(routingContext, "param0");

        if (user_ids.isEmpty() && !userId.isEmpty()) {
            user_ids = userId;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(user_ids);

        if (!user_ids.isEmpty()) {

            List<Long> dealerList = Arrays.stream(user_ids.split(","))
                    .map(pid -> {return Long.parseLong(pid);})
                    .collect(Collectors.toList());


            ChainSerialization chainSerialization =
                    ChainSerialization.create(routingContext.getDelegate());

            this.chuheDbService.deleteUserBatch(dealerList, reply -> {

                if (reply.succeeded()) {

                    String message = "成功删除 " + dealerList.size() + " 个客户 [" + builder.toString() + "]";

                    chainSerialization.setStatusRealCode(200)
                            .putMessage(message)
                            .putFlashMessage(message);

                } else {
                    chainSerialization
                            .putMessage("删除失败")
                            .putFlashMessage("删除失败")
                            .putFlashException(reply.cause());
                }

                chainSerialization.redirect("/mans/customer");

            });

        }

    }


    public void customerDetail(RoutingContext routingContext) {

        Long customerId = getLong(routingContext,"param0");
        String detailOrEditPage = routingContext.normalisedPath().endsWith("/edit") ? "edit" : "detail";

        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate())
                        .putViewName("/man/customer/customer_" + detailOrEditPage + ".html");

        this.chuheDbService.userDetail(customerId, reply -> {

            if (reply.succeeded()) {

                if (reply.result().fieldNames().size() > 0) {
                    chainSerialization.putContextData(reply.result())
                            .putMessage("客户详情");
                } else {
                    chainSerialization
                            .setStatusRealCode(404)
                            .putFlashMessage("你查看的客户不存在")
                            .putContextData(null)
                            .redirect("/not-found", true);
                    return;
                }

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

    public void customerUpdate(RoutingContext routingContext) {

        Long customerId = Long.parseLong(routingContext.pathParam("param0"));

        JsonObject customer = getCustomerObject(routingContext);

        this.chuheDbService.updateUser(customerId, customer, reply -> {

            if (reply.succeeded()) {

                String message = "成功更新一个客户 [" + customerId + "]";

                ChainSerialization.create(routingContext.getDelegate())
                        .setStatusRealCode(200)
                        .putMessage(message)
                        .putFlashMessage(message)
                        .redirect(routingContext.normalisedPath());
            } else {
                ChainSerialization.create(routingContext.getDelegate())
                        // .putFlashMessage(reply.cause().getMessage())
                        .putFlashException(reply.cause())
                        .redirect(routingContext.normalisedPath());
            }
        });
    }

}
