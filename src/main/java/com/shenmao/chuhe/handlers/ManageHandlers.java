package com.shenmao.chuhe.handlers;

import com.google.common.base.Strings;
import com.shenmao.chuhe.database.chuhe.ChuheDbService;
import com.shenmao.chuhe.exceptions.PurposeException;
import com.shenmao.chuhe.serialization.ChainSerialization;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ManageHandlers {


    private static final Logger LOGGER = LoggerFactory.getLogger(ManageHandlers.class);

    public static ManageHandlers create(ChuheDbService chuheDbService) {

        return new ManageHandlers(chuheDbService);
    }

    ChuheDbService chuheDbService;

    public ManageHandlers(ChuheDbService chuheDbService) {
        this.chuheDbService = chuheDbService;
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

            if (reply.succeeded()) {
                chainSerialization.putContextData(reply.result());
            } else {
                chainSerialization.putMessage("未能取得产品列表");
                chainSerialization.putException(reply.cause());
            }

            chainSerialization.serialize();

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
//                        .putFlashMessage(reply.cause().getMessage())
                        .putFlashException(reply.cause())
                        .redirect("/mans/products");
            }
        });

    }

}
