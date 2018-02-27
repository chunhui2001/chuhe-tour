package com.shenmao.chuhe.serialization;

import com.shenmao.chuhe.commons.handlebarhelper.SpinnerHelper;
import com.shenmao.chuhe.exceptions.HbsTemplateParseException;
import com.shenmao.chuhe.commons.handlebarhelper.CompareHelper;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import io.vertx.ext.web.templ.TemplateEngine;

import java.io.PrintWriter;
import java.io.StringWriter;

public class HtmlSerialization {




  public static HandlebarsTemplateEngine getHandlebarsTemplateEngine() {
    HandlebarsTemplateEngine handlebarsTemplateEngine = HandlebarsTemplateEngine.create().setExtension("html");
    handlebarsTemplateEngine.getHandlebars()
      .registerHelper("compare", new CompareHelper())
      .registerHelper("spinner", new SpinnerHelper());
    return handlebarsTemplateEngine;
  }

  // http://jknack.github.io/handlebars.java/reuse.html
  public static final TemplateEngine templateEngine = getHandlebarsTemplateEngine();

  // public static final TemplateHandler htmlTemplateHandler = TemplateHandler.create(templateEngine);
  public static final String templateFolderName = "templates.a2";


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

    int code = context.get("statusRealCode") != null ? context.get("statusRealCode") : context.statusCode();
    String errorTrace = context.get("errorTrace") != null ? context.get("errorTrace").toString() : null;

    StringWriter exceptionTrace = new StringWriter();
    Throwable exception =  context.get("exception");
    String flashException = context.session().get("flashException");


    if (exception != null || flashException != null) {

        if (flashException != null) {
            errorTrace = flashException;
            context.session().remove("flashException");
        } else {
            exception.printStackTrace(new PrintWriter(exceptionTrace));
            errorTrace = exceptionTrace.toString();
        }

        context.put("errorTrace", errorTrace);
        if (code == -1) code = 501;
    }

    code = code == -1 ? 200 : code;

    String flashMessage = context.session().get("flashMessage");
    String viewMessage = context.response().getStatusMessage();

    if (flashMessage != null) {
        context.session().remove("flashMessage");
        context.put("flashMessage", flashMessage);
    }

    context.response().putHeader("Content-Type", "text/html;charset=UTF-8");
    context.put("viewError", errorTrace != null || exception != null);
    context.put("viewCode", code);
    context.put("viewData", viewData);
    context.put("viewUser", viewUser);
    context.put("viewMessage", viewMessage);

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
