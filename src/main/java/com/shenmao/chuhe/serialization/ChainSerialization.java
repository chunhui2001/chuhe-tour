package com.shenmao.chuhe.serialization;

import com.shenmao.chuhe.exceptions.PurposeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ChainSerialization {

  RoutingContext _context;

  private ChainSerialization(RoutingContext context) {

    this._context = context;
  }

  public static ChainSerialization create(RoutingContext context) {

    SerializeOptions serializeOptions = context.get("serializeOptions");

    if (serializeOptions == null) {
      serializeOptions = SerializeOptions.create(context, SerializeType.HTML);
    }

    return new ChainSerialization(context);

  }

  public void serialize() {

    SerializeOptions serializeOptions = (SerializeOptions)this._context.data().get("serializeOptions");

    switch (serializeOptions.getType()) {
      case HTML:
        HtmlSerialization.serialize(this._context, serializeOptions);
        break;
      case JSON:
        JsonSerialization.serialize(this._context, serializeOptions);
        break;
      case CSV:
        CsvSerialization.serialize(this._context, serializeOptions);
        break;
      case XML:
        XmlSerialization.serialize(this._context, serializeOptions);
        break;
      case WS:
        WebSocketSerialization.serialize(this._context, serializeOptions);
        break;
      case TEXT:
        break;
      default:
          throw new IllegalArgumentException("Invalid Accept type: " + serializeOptions.getType());
    }

  }

  public ChainSerialization putViewName(String viewName) {
    SerializeOptions serializeOptions = this._context.get("serializeOptions");
    serializeOptions.putConfig("viewName", viewName);
    return this;
  }

  public ChainSerialization putContextData(Object contextData) {
    SerializeOptions serializeOptions = this._context.get("serializeOptions");
    serializeOptions.putConfig("contextData", contextData);
    return this;
  }

  public ChainSerialization setSerializeType(SerializeType type) {
    SerializeOptions serializeOptions = this._context.get("serializeOptions");
    serializeOptions.setType(type);
    return this;
  }

  public ChainSerialization setStatusCode(int code) {
    this._context.response().setStatusCode(code);
    return this;
  }

  public ChainSerialization setStatusRealCode(int code) {
    this._context.put("statusRealCode", code);
    return this;
  }

  public ChainSerialization putMessage(String msg) {
    this._context.response().setStatusMessage(msg);
    return this;
  }

  public ChainSerialization putFlashMessage(String msg) {

    if(! "XMLHttpRequest".equals(this._context.request().getHeader("X-Requested-With"))) {
      this._context.session().put("flashMessage", msg);
    }

    this.putMessage(msg);
    return this;
  }

  public ChainSerialization putException(Throwable exception) {
    this._context.put("exception", exception);
    return this;
  }

  public ChainSerialization putFlashException(Throwable exception) {
    StringWriter exceptionTrace = new StringWriter();
    exception.printStackTrace(new PrintWriter(exceptionTrace));
    this._context.session().put("flashException", exceptionTrace.toString());
    return this;
  }

  public ChainSerialization putErrorTrace(String error) {
    this._context.put("errorTrace", error);
    return this;
  }

  public ChainSerialization putExceptionClass(String className) {
    this._context.put("exceptionClassName", className);
    return this;
  }


  public ChainSerialization setWsEndpoint(String endpoint) {
    SerializeOptions serializeOptions = this._context.get("serializeOptions");
    serializeOptions.putConfig("ws_endpoint", endpoint);
    return this;
  }

  public void redirect(String url, boolean reroute) {

    SerializeOptions serializeOptions = (SerializeOptions)this._context.data().get("serializeOptions");

    if (serializeOptions.getType() == SerializeType.HTML) {
      if (reroute) {
        this._context.reroute(url);
      } else {
        this._context.response().setStatusCode(302).putHeader("Location", url).end();
      }
      return;
    }

    this.serialize();

  }


  public void redirect(String url) {
    redirect(url, false);
  }

}
