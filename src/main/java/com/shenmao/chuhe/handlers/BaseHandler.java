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
        String value = "";

        if (jsonData != null && jsonData.containsKey(key)) {
            value = jsonData.getString(key);
        }

        MultiMap requestBody = getFormData(routingContext);
        if (requestBody != null && requestBody.contains(key)) {
            value = routingContext.request().getParam(key);
        }

        if (value.equals("无")) {
            value = "";
        }

        return value;
    }

    public boolean paramExists(RoutingContext routingContext, String key) {

        JsonObject jsonData = getJsonData(routingContext);
        MultiMap requestBody = getFormData(routingContext);

        if (jsonData != null || requestBody != null) {
            if ( (jsonData != null && jsonData.containsKey(key))
                    || (requestBody != null && requestBody.contains(key))) {
                return true;
            }
        }

        return false;

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

    public Integer getInteger(RoutingContext routingContext, String key) {

        JsonObject jsonData = getJsonData(routingContext);

        if (jsonData != null && jsonData.containsKey(key)) {
            return jsonData.getInteger(key);
        }

        MultiMap requestBody = getFormData(routingContext);

        if (requestBody != null && requestBody.contains(key)
                && !routingContext.request().getParam(key).trim().isEmpty()) {
            return Integer.parseInt(routingContext.request().getParam(key).trim());

        }

        return null;
    }

    public Long getLong(RoutingContext routingContext, String key) {
        Integer value = getInteger(routingContext, key);
        if (value == null) return null;
        return Long.parseLong(value + "");
    }

    public JsonObject getJson(RoutingContext routingContext, String key) {

        JsonObject jsonData = getJsonData(routingContext);
        String jsonString = null;

        if (jsonData != null && jsonData.containsKey(key)) {
            jsonString = jsonData.getString(key);
        }

        MultiMap requestBody = getFormData(routingContext);

        if (requestBody != null && requestBody.contains(key)
                && !routingContext.request().getParam(key).trim().isEmpty()) {
            jsonString = routingContext.request().getParam(key).trim();

        }

        return new JsonObject(jsonString);
    }

}
