package com.shenmao.chuhe.handlers;

import com.shenmao.chuhe.serialization.ChainSerialization;
import com.shenmao.chuhe.serialization.SerializeType;
import com.shenmao.chuhe.exceptions.PurposeException;
import com.shenmao.chuhe.serialization.SerializeOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.io.PrintWriter;
import java.io.StringWriter;

public class GlobalHandlers {

  public static final String _SUPPORT_EXTS_PATTERN = "(.html|.htm|.json|.xml|.csv)?";
  public static final String _SUPPORT_EXTS_PATTERN2 = "^.*(\\.html|\\.htm|\\.json|\\.xml|\\.csv)$";
  public static final String _SUPPORT_RESOURCE_EXTS_PATTERN = "^.*(\\.jpg|\\.jpeg|\\.png|\\.gif|\\.css|\\.js|\\.ppt|\\.pdf|\\.png)$";

  public static GlobalHandlers create() {
    return new GlobalHandlers();
  }

  public GlobalHandlers() {


  }

  public void htmlRenderHandler(RoutingContext routingContext) {
    SerializeOptions.create(routingContext.getDelegate(), SerializeType.HTML);
    routingContext.next();
  }

  public void jsonSerializationHandler(RoutingContext routingContext) {
    SerializeOptions.create(routingContext.getDelegate(), SerializeType.JSON);
    routingContext.next();
  }

  public void xmlSerializationHandler(RoutingContext routingContext) {
    SerializeOptions.create(routingContext.getDelegate(), SerializeType.XML);
    routingContext.next();
  }

  public void csvSerializationHandler(RoutingContext routingContext) {
    SerializeOptions.create(routingContext.getDelegate(), SerializeType.CSV);
    routingContext.next();
  }

  public void exceptionHandler(RoutingContext routingContext) {

    // 401 handler
    if (routingContext.statusCode() == 401) {
      // This prompts the browser to show a log-in dialog and prompt the user to enter their username and password
      routingContext.response().setStatusCode(401);
      routingContext.response().end();
      return;
    }

    if (routingContext.statusCode() == 404) {
      notFoundHandler(routingContext);
      return;
    }

    if (routingContext.statusCode() == 406) {
      errorPageHandler(routingContext, new PurposeException(406, "Operation not supported"));
      return;
    }

    if (routingContext.statusCode() == 403) {

      /* routingContext.response().setStatusCode(302);
      routingContext.response().putHeader("Location", "/login");
      routingContext.response().end(); */

      ChainSerialization
              .create(routingContext.getDelegate())
              .putFlashMessage("用户名或密码错误!")
              .redirect("login");
      return;
    }

    if (routingContext.statusCode() == 503) {
      errorPageHandler(routingContext, new PurposeException(503, "Time out request"));
      return;
    }

    errorPageHandler(routingContext);

  }


  public void errorPageHandler(RoutingContext routingContext) {
    errorPageHandler(routingContext, null);
  }

  public void errorPageHandler(RoutingContext routingContext, PurposeException exception) {

    PurposeException purposeException = exception == null ? null : exception;
    String exceptionClassName = null;



    if (purposeException == null && routingContext.failure() instanceof  PurposeException) {
      purposeException = (PurposeException) routingContext.failure();
    }

    String errorMessage = ( purposeException == null ? routingContext.failure().getMessage() : purposeException.getMessage());
    StringWriter errorsTrace = new StringWriter();
    int errorCode = 500;

    if (purposeException != null) {
      exceptionClassName = purposeException.getClass().getName();
      purposeException.printStackTrace(new PrintWriter(errorsTrace));
      errorCode = purposeException.getErrorCode();
    } else {
      exceptionClassName = routingContext.failure().getClass().getName();
      routingContext.failure().printStackTrace(new PrintWriter(errorsTrace));
    }

    System.out.println(exceptionClassName + ", exceptionName");

    ChainSerialization.create(routingContext.getDelegate())
      .setStatusCode(201)
      .setStatusRealCode(errorCode)
      .putViewName("/500.html")
      .putFlashMessage(errorMessage)
      .putContextData(new JsonObject())
      .putErrorTrace(errorsTrace.toString())
      .putExceptionClass(exceptionClassName)
      .serialize();

  }

  public void notFoundHandler(RoutingContext routingContext) {

    if (routingContext.normalisedPath().matches(_SUPPORT_RESOURCE_EXTS_PATTERN)) {
      routingContext.response().setStatusCode(404).end();
      return;
    }

    ChainSerialization.create(routingContext.getDelegate())
      .setStatusCode(202)
      .setStatusRealCode(404)
      .putViewName("/404.html")
      .serialize();

  }

  public void notSupportedExtHandler(RoutingContext routingContext) {
    throw new PurposeException(406, "Extension not supported");
    //notFoundHandler(routingContext);
  }



  public void notSupportExtensionMiddleware(RoutingContext routingContext) {

    String path = routingContext.normalisedPath().trim();

    if (path.indexOf(".") == -1 || path.matches(_SUPPORT_EXTS_PATTERN2)) {
      routingContext.next();
      return;
    }

    if (path.indexOf(".") == -1 || path.matches(_SUPPORT_RESOURCE_EXTS_PATTERN)) {
      routingContext.next();
      return;
    }

    this.notSupportedExtHandler(routingContext);

  }



}
