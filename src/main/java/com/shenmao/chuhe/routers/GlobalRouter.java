package com.shenmao.chuhe.routers;

import com.shenmao.chuhe.exceptions.PurposeException;
import com.shenmao.chuhe.handlers.GlobalHandlers;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.AuthHandler;

public class GlobalRouter implements ChuheRouter {


    Vertx vertx;
    Router router;
    AuthHandler authHandler = null;
    GlobalHandlers globalHandlers = null;

    public GlobalRouter (Vertx vertx, AuthHandler authHandler) {

        this.vertx = vertx;
        this.router = Router.router(vertx);
        this.authHandler = authHandler;
        this.globalHandlers = GlobalHandlers.create();

        this.globalRouter();

    }

    public Router getRouter() {
        return this.router;
    }

    private void globalRouter() {




        // throw error purpose
        this.router.routeWithRegex(HttpMethod.GET, "/throw" + GlobalHandlers._SUPPORT_EXTS_PATTERN).handler(routingContext -> {
            throw new PurposeException();
            //throw new PurposeException("Throw an exception purpose!");
        });

        // 404 notfound page router
        this.router.routeWithRegex(HttpMethod.GET, "/not-found" + GlobalHandlers._SUPPORT_EXTS_PATTERN).handler(routingContext -> {
            routingContext.fail(404);
        });

        // 500 handler
        this.router.routeWithRegex(HttpMethod.GET, "/error" + GlobalHandlers._SUPPORT_EXTS_PATTERN).handler(globalHandlers::errorPageHandler);

        // not have route matchs
        this.router.route("/*").handler(globalHandlers::notFoundHandler);

    }


    public static String getRouter(String... str) {
        String s = (str == null || str.length == 0) ? "" : "/" + String.join("/", str) ;
        return  s + GlobalHandlers._SUPPORT_EXTS_PATTERN;
    }



}
