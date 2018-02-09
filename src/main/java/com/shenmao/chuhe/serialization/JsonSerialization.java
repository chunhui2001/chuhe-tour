package com.shenmao.chuhe.serialization;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.io.PrintWriter;
import java.io.StringWriter;

public class JsonSerialization {

  public static void serialize(RoutingContext context, SerializeOptions options) {
    context.response().putHeader("Content-Type", "application/json;charset=UTF-8");
    context.response().end(getData(context, options).encode());
  }

  public static JsonObject getData(RoutingContext context, SerializeOptions options) {

    Object contextData = options.config("contextData");
    int realcode = context.get("statusRealCode") != null ? (int)context.get("statusRealCode") : context.statusCode();
    String errorTrace = context.get("errorTrace") != null ? context.get("errorTrace").toString() : null;

    StringWriter exceptionTrace = new StringWriter();
    Throwable exception =  context.get("exception");

    if (exception != null) {
      exception.printStackTrace(new PrintWriter(exceptionTrace));
      errorTrace = exceptionTrace.toString();
      if (realcode == -1) realcode = 501;
    }

    realcode = realcode == -1 ? 200 : realcode;

    context.response().setStatusCode(200);

    JsonObject jsonObject = new JsonObject()
      .put("error", errorTrace != null || exception != null)
      .put("code", realcode)
      .put("message", context.response().getStatusMessage())
      .put("data", contextData);

    if (errorTrace != null) {
      jsonObject.put("errorTrace", errorTrace);
    }

    return jsonObject;
  }

}
