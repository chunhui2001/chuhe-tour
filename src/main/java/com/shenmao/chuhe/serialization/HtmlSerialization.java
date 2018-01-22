package com.shenmao.chuhe.serialization;

import com.shenmao.chuhe.exceptions.HbsTemplateParseException;
import com.shenmao.chuhe.commons.handlebarhelpers.CompareHelper;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import io.vertx.ext.web.templ.TemplateEngine;

public class HtmlSerialization {




  public static HandlebarsTemplateEngine getHandlebarsTemplateEngine() {
    HandlebarsTemplateEngine handlebarsTemplateEngine = HandlebarsTemplateEngine.create().setExtension("html");
    handlebarsTemplateEngine.getHandlebars()
      .registerHelper("compare", new CompareHelper());
    return handlebarsTemplateEngine;
  }

  // http://jknack.github.io/handlebars.java/reuse.html
  public static final TemplateEngine templateEngine = getHandlebarsTemplateEngine();

  // public static final TemplateHandler htmlTemplateHandler = TemplateHandler.create(templateEngine);
  public static final String templateFolderName = "templates";


  public static void serialize(RoutingContext context, SerializeOptions options) {

    JsonObject user = new JsonObject()
      .put("isAuthenticated", context.user() != null)
      .put("username", context.user() != null ? context.user().principal().getString("username") : null);

    serialize(context,
      (String)options.config("viewName"),
      options.config("contextData"),
      user);
  }

  public static void serialize(RoutingContext context, String viewName, Object viewData, JsonObject viewUser) {

    if (context.normalisedPath().endsWith("/xhr_send")
      || context.normalisedPath().endsWith("/websocket")
      || context.normalisedPath().endsWith("/xhr_streaming")) {
      context.next();
      return;
    }

    context.response().putHeader("Content-Type", "text/html;charset=UTF-8");
    context.put("viewData", viewData);
    context.put("viewUser", viewUser);
    context.put("viewMessage", context.response().getStatusMessage());

    if (context.get("X-XSRF-TOKEN") != null) {
      context.put("xsrfToken", context.get("X-XSRF-TOKEN"));
    }

    templateEngine.render(context, templateFolderName, viewName, ar -> {

      if (ar.succeeded()) {
        context.response().end(ar.result());
      } else {
        throw new HbsTemplateParseException(ar.cause().getMessage());
      }

    });

  }

}
