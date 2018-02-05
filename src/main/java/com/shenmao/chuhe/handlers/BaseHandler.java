package com.shenmao.chuhe.handlers;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.ext.web.RoutingContext;

public class BaseHandler {

    private JsonObject getJsonData(RoutingContext routingContext) {

        JsonObject requestData = null;

        try {
            requestData = routingContext.getBodyAsJson();
        } catch (Exception e) {

        }

        return requestData;
    }

    private MultiMap getFormData(RoutingContext routingContext) {
        MultiMap requestBody =  routingContext.request().params();
        if (requestBody != null && requestBody.size() > 0) {
            return requestBody;
        }
        return null;
    }

    public String getString(RoutingContext routingContext, String key) {

        JsonObject jsonData = getJsonData(routingContext);

        if (jsonData != null && jsonData.containsKey(key)) {
            return jsonData.getString(key);
        }

        MultiMap requestBody = getFormData(routingContext);
        if (requestBody != null && requestBody.contains(key)) {
            return routingContext.request().getParam(key);
        }

        return "";
    }

    public Double getDouble(RoutingContext routingContext, String key) {

        JsonObject jsonData = getJsonData(routingContext);

        if (jsonData != null && jsonData.containsKey(key)) {
            return jsonData.getDouble(key);
        }

        MultiMap requestBody = getFormData(routingContext);

        if (requestBody != null && requestBody.contains(key)
                && !routingContext.request().getParam(key).trim().isEmpty()) {
            return Double.parseDouble(routingContext.request().getParam(key).trim());

        }

        return null;
    }

}
