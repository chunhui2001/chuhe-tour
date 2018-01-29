package com.shenmao.chuhe.handlers.manage;

import com.google.common.base.Strings;
import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ManageProductsHandlers {


    private static final Logger LOGGER = LoggerFactory.getLogger(ManageProductsHandlers.class);

    public static ManageProductsHandlers create(ChuheDbService chuheDbService) {

        return new ManageProductsHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageProductsHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
    }

    /**
     * 商品详细信息
     * @param routingContext
     */
    public void productDetail(RoutingContext routingContext) {

        Long productId = Long.parseLong(routingContext.request().getParam("param0"));
        String detailOrEditPage = routingContext.normalisedPath().endsWith("/edit") ? "edit" : "detail";


        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/products/products_" + detailOrEditPage + ".html")
                .putMessage("所有商品列表");

        this.chuheDbService.fetchProductById(productId, reply -> {

            if (!reply.succeeded()) {
                chainSerialization
                        .putMessage("未能取得产品详情")
                        .putException(reply.cause()).serialize();
                return;
            }

            chainSerialization.putContextData(reply.result()).serialize();

        });
    }

    /**
     * 所有商品列表,
     * @param routingContext
     */
    public void productsList(RoutingContext routingContext) {

        ChainSerialization chainSerialization = ChainSerialization.create(routingContext.getDelegate())
                .putViewName("/man/products/products_index.html")
                .putMessage("所有商品列表");

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


    /**
     * 保存一个商品,
     * @param routingContext
     */
    public void productsSave(RoutingContext routingContext) {

        String productName = Strings.nullToEmpty(routingContext.request().getParam("product_name")).trim();
        String productUnit = Strings.nullToEmpty(routingContext.request().getParam("product_unit")).trim();
        Double productPrice = Double.parseDouble(Strings.nullToEmpty(routingContext.request().getParam("product_price")).trim());
        String productSpec = Strings.nullToEmpty(routingContext.request().getParam("product_spec")).trim();
        String productDesc = Strings.nullToEmpty(routingContext.request().getParam("product_desc")).trim();


        JsonObject product = new JsonObject()
                    .put("productName", productName)
                .put("productUnit", productUnit)
                .put("productPrice", productPrice)
                .put("productSpec", productSpec)
                .put("productDesc", productDesc);

        chuheDbService.createProducts(product, reply -> {

            if (reply.succeeded()) {
                // Long newProductId = reply.result();
                ChainSerialization.create(routingContext.getDelegate())
                        .putFlashMessage("成功添加一个产品")
                        .redirect("/mans/products");
            } else {
                ChainSerialization.create(routingContext.getDelegate())
                        // .putFlashMessage(reply.cause().getMessage())
                        .putFlashException(reply.cause())
                        .redirect("/mans/products");
            }
        });

    }

    public void productDelete(RoutingContext routingContext) {

        Long productId = Long.parseLong(routingContext.pathParam("param0"));
        // String productName = routingContext.queryParam("name").get(0);

        this.chuheDbService.deleteProductById(productId, reply -> {

            if (reply.succeeded()) {
                ChainSerialization.create(routingContext.getDelegate())
                        .putFlashMessage("成功删除一个产品 [" + productId + "]")
                        .redirect("/mans/products");
            } else {
                ChainSerialization.create(routingContext.getDelegate())
                        .putFlashException(reply.cause())
                        .redirect("/mans/products");
            }

        });

    }

    public void productUpdate(RoutingContext routingContext) {

        Long productId = Long.parseLong(routingContext.pathParam("param0"));

        ChainSerialization.create(routingContext.getDelegate())
                .putFlashMessage("成功保存一个产品 [" + productId + "]")
                .redirect(routingContext.normalisedPath());

        //routingContext.response().end(productId + "," + routingContext.request().method());
    }


}
