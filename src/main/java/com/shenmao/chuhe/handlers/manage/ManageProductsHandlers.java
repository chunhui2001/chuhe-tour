package com.shenmao.chuhe.handlers.manage;

import com.google.common.base.Strings;
import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.handlers.BaseHandler;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rx.java.RxHelper;
import io.vertx.rx.java.SingleOnSubscribeAdapter;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Single;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ManageProductsHandlers extends BaseHandler {


    private static final Logger LOGGER = LoggerFactory.getLogger(ManageProductsHandlers.class);

    public static ManageProductsHandlers create(ChuheDbService chuheDbService) {

        return new ManageProductsHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageProductsHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }

    /**
     * 产品详细信息
     * @param routingContext
     */
    public void productDetail(RoutingContext routingContext) {

        boolean isEdit = routingContext.normalisedPath().endsWith("/edit");
        Long productId = Long.parseLong(routingContext.request().getParam("param0"));
        String detailOrEditPage = isEdit ? "new" : "detail";

        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/products/product_" + detailOrEditPage + ".html")
                .putMessage("产品详细信息");

        this.chuheDbService.fetchProductById(productId, reply -> {

            if (!reply.succeeded()) {
                chainSerialization
                        .putMessage("未能取得产品详情")
                        .putException(reply.cause()).serialize();
                return;
            }

            if (reply.result().fieldNames().size() == 0) {
                chainSerialization
                        .setStatusRealCode(404)
                        .putFlashMessage("你访问的产品不存在")
                        .putContextData(null)
                        .redirect("/not-found", true);
                return;
            }

            if (isEdit) {
                routingContext.put("page_title", "编辑产品信息");
            }

            routingContext.put("product_model_json", reply.result().encode());

            chainSerialization.putContextData(reply.result()).serialize();

        });
    }

    /**
     * 所有产品列表,
     * @param routingContext
     */
    public void productsList(RoutingContext routingContext) {

        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/products/product_index.html")
                .putMessage("所有产品列表");

        chuheDbService.fetchAllProducts(reply -> {

            if (!reply.succeeded()) {
                chainSerialization
                        .putMessage("未能取得产品列表")
                        .putException(reply.cause())
                        .serialize();
                return;
            }

            List<JsonObject> productList = reply.result();//.stream().collect(Collectors.toList());

            chainSerialization.putContextData(productList).serialize();

        });

    }


    public void newProduct(RoutingContext routingContext) {

        String pageTitle = "新增产品";

        ChainSerialization chainSerialization =
                ChainSerialization.create(routingContext.getDelegate());

        routingContext.put("page_title", pageTitle);

        chainSerialization.putContextData(new JsonObject())
                .putViewName("/man/products/product_new.html")
                .putMessage(pageTitle);

        chainSerialization.serialize();

    }

    private JsonObject getProductObject(RoutingContext routingContext) {

        JsonObject result = new JsonObject();

        if (paramExists(routingContext, "product_name")) {
            result.put("product_name", getString(routingContext,"product_name"));
        }

        if (paramExists(routingContext, "product_type")) {
            result.put("product_type", getString(routingContext,"product_type"));
        }

        if (paramExists(routingContext, "product_unit")) {
            result.put("product_unit", getString(routingContext,"product_unit"));
        }

        if (paramExists(routingContext, "product_price")) {
            result.put("product_price", getString(routingContext,"product_price"));
        }

        if (paramExists(routingContext, "product_spec")) {
            result.put("product_spec", getString(routingContext,"product_spec"));
        }

        if (paramExists(routingContext, "product_desc")) {
            result.put("product_desc", getString(routingContext,"product_desc"));
        }


        return result;
    }


    /**
     * 保存一个产品,
     * @param routingContext
     */
    public void productsSave(RoutingContext routingContext) {

        JsonObject product = getProductObject(routingContext);

        chuheDbService.createProducts(product, reply -> {

            if (reply.succeeded()) {
                // Long newProductId = reply.result();
                ChainSerialization.create(routingContext.getDelegate())
                        .putContextData(reply.result())
                        .putFlashMessage("成功添加一个产品")
                        .putMessage("添加成功返回新产品ID")
                        .redirect("/mans/products");
            } else {
                ChainSerialization.create(routingContext.getDelegate())
                        // .putFlashMessage(reply.cause().getMessage())
                        .putMessage(reply.cause().getMessage())
                        .putException(reply.cause())
                        .putFlashException(reply.cause())
                        .redirect("/mans/products/new");
            }
        });

    }

    public void productDelete(RoutingContext routingContext) {

        Long productId = Long.parseLong(routingContext.pathParam("param0"));
        Future<Integer> future = productDeleteById(productId);

        future.setHandler(ar -> {

            if (ar.succeeded() || ar.cause() == null) {

                String message = ar.result() > 0 ? "成功删除一个产品 [" + productId + "]" : "产品不存在 [" + productId + "]";

                ChainSerialization.create(routingContext.getDelegate())
                        .setStatusRealCode(ar.result() > 0 ? 200 : 202)
                        .putMessage(message)
                        .putFlashMessage(message)
                        .redirect("/mans/products");

            } else {
                ChainSerialization.create(routingContext.getDelegate())
                        .putFlashException(ar.cause())
                        .redirect("/mans/products");
            }

        });




    }

    public void productDeleteBatch(RoutingContext routingContext) {

        // https://streamdata.io/blog/vert-x-and-the-async-calls-chain/
        String product_ids = getString(routingContext, "product_ids");

        if (!product_ids.isEmpty()) {

            List<Long> productList = Arrays.stream(product_ids.split(","))
                    .map(pid -> {return Long.parseLong(pid);})
                    .collect(Collectors.toList());

            this.chuheDbService.deleteProductBatch(productList, reply -> {

                if (reply.succeeded()) {

                    String message = "成功删除 " + productList.size() + " 个产品 [" + product_ids + "]";

                    ChainSerialization.create(routingContext.getDelegate())
                            .setStatusRealCode(200)
                            .putMessage(message)
                            .putFlashMessage(message)
                            .redirect("/mans/products");

                } else {
                    ChainSerialization.create(routingContext.getDelegate())
                            .putMessage("删除失败")
                            .putFlashMessage("删除失败")
                            .putFlashException(reply.cause())
                            .redirect("/mans/products");
                }

            });

        }


    }

    private  Future<Integer> productDeleteById(Long productId) {

        Future<Integer> future = Future.future();


        this.chuheDbService.deleteProductById(productId, reply -> {
            if (reply.succeeded()) {
                System.out.println("success2");
                future.complete(reply.result());
            }
            else {
                System.out.println("failed2");
                future.fail(reply.cause());
            }
        });

        return future;

    }

    public void productUpdate(RoutingContext routingContext) {

        Long productId = Long.parseLong(routingContext.pathParam("param0"));

        JsonObject product = getProductObject(routingContext);

        this.chuheDbService.updateProduct(productId, product, reply -> {

            if (reply.succeeded()) {

                String message = reply.result() > 0 ? "成功更新一个产品 [" + productId + "]" : "内容无更新或产品不存在 [" + productId + "]";

                ChainSerialization.create(routingContext.getDelegate())
                        .setStatusRealCode(reply.result() > 0 ? 200 : 202)
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
