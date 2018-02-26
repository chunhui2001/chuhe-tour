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

public class ManageDealerHandlers extends BaseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageDealerHandlers.class);


    public static ManageDealerHandlers create(ChuheDbService chuheDbService) {
        return new ManageDealerHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageDealerHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }


    public void dealerIndex(RoutingContext routingContext) {

        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate());

        this.chuheDbService.fetchAllDealers(reply -> {

            if (reply.succeeded()) {

                chainSerialization.putContextData(reply.result())
                                .putViewName("/man/dealer/dealer_index.html")
                                .putMessage("经销商管理");

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

    public void dealerDetail(RoutingContext routingContext) {

        Long dealerId = getLong(routingContext,"param0");
        String detailOrEditPage = routingContext.normalisedPath().endsWith("/edit") ? "edit" : "detail";


        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate())
                        .putViewName("/man/dealer/dealer_" + detailOrEditPage + ".html");

        this.chuheDbService.dealerDetail(dealerId, reply -> {

            if (reply.succeeded()) {

                if (reply.result().fieldNames().size() > 0) {
                    chainSerialization.putContextData(reply.result())
                            .putMessage("经销商详情");
                } else {
                    chainSerialization
                            .setStatusRealCode(404)
                            .putFlashMessage("你查看的经销商不存在")
                            .putContextData(null)
                            .redirect("/not-found", true);
                    return;
                }

            } else {
                chainSerialization
                        .putFlashMessage(reply.cause().getMessage())
                        .putMessage(reply.cause().getMessage())
                        .putException(reply.cause())
                        .putFlashException(reply.cause());;
            }


            chainSerialization.serialize();
        });

    }

    public JsonObject getDealerObject(RoutingContext routingContext) {

        JsonObject dealer = new JsonObject();

        dealer.put("dealer_name", getString(routingContext, "dealer_name"));
        dealer.put("dealer_gender", getString(routingContext, "dealer_gender"));
        dealer.put("dealer_identity", getString(routingContext, "dealer_identity"));
        dealer.put("dealer_source_from", getString(routingContext, "dealer_source_from"));
        dealer.put("dealer_phone", getString(routingContext, "dealer_phone"));
        dealer.put("dealer_home_tel", getString(routingContext, "dealer_home_tel"));
        dealer.put("dealer_home_address", getString(routingContext, "dealer_home_address"));
        dealer.put("dealer_wchat_id", getString(routingContext, "dealer_wchat_id"));
        dealer.put("dealer_level", getString(routingContext, "dealer_level"));
        dealer.put("dealer_scope", getString(routingContext, "dealer_scope"));
        dealer.put("dealer_desc", getString(routingContext, "dealer_desc"));

        return dealer;

    }

    public void newDealer(RoutingContext routingContext) {

        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate());

        chainSerialization.putContextData(new JsonObject())
                .putViewName("/man/dealer/dealer_new.html")
                .putMessage("新增经销商");

        chainSerialization.serialize();
    }

    public void createDealer(RoutingContext routingContext) {

        JsonObject dealer = getDealerObject(routingContext);

        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate());

        JsonArray roles = new JsonArray();

        roles.add("role_user");
        roles.add("role_dealer");


        this.chuheDbService.createDealer(dealer, roles, reply -> {

            if (reply.succeeded()) {
                chainSerialization
                        .putContextData(reply.result())
                        .putFlashMessage("成功添加一个经销商")
                        .putMessage("成功添加一个经销商");

                chainSerialization.redirect("/mans/dealer");
            } else {
                chainSerialization
                        .putFlashMessage(reply.cause().getMessage())
                        .putMessage(reply.cause().getMessage())
                        .putException(reply.cause())
                        .putFlashException(reply.cause());

                chainSerialization.redirect("/mans/dealer/new");
            }


        });

    }

    public void deleteDealerBatch(RoutingContext routingContext) {

        String dealer_ids = getString(routingContext, "dealer_ids");
        String dealerId = getString(routingContext, "param0");

        if (dealer_ids.isEmpty() && !dealerId.isEmpty()) {
            dealer_ids = dealerId;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(dealer_ids);

        if (!dealer_ids.isEmpty()) {

            List<Long> dealerList = Arrays.stream(dealer_ids.split(","))
                    .map(pid -> {return Long.parseLong(pid);})
                    .collect(Collectors.toList());


            ChainSerialization chainSerialization =
                    ChainSerialization.create(routingContext.getDelegate());

            this.chuheDbService.deleteDealerBatch(dealerList, reply -> {

                if (reply.succeeded()) {

                    String message = "成功删除 " + dealerList.size() + " 个经销商 [" + builder.toString() + "]";

                    chainSerialization.setStatusRealCode(200)
                            .putMessage(message)
                            .putFlashMessage(message);

                } else {
                    chainSerialization
                            .putMessage("删除失败")
                            .putFlashMessage("删除失败")
                            .putFlashException(reply.cause());
                }

                chainSerialization.redirect("/mans/dealer");

            });

        }

    }

    public void dealerUpdate(RoutingContext routingContext) {


        Long dealerId = Long.parseLong(routingContext.pathParam("param0"));

        JsonObject dealer = getDealerObject(routingContext);

        this.chuheDbService.updateDealer(dealerId, dealer, reply -> {

            if (reply.succeeded()) {

                String message = "成功更新一个经销商 [" + dealerId + "]";

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
