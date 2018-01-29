package com.shenmao.chuhe.routers;

import com.shenmao.chuhe.exceptions.PurposeException;
import com.shenmao.chuhe.handlers.GlobalHandlers;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.Vertx;
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


        this.router.route("/*").handler(routingContext -> {

            if (routingContext.request().method() == HttpMethod.GET) {
                routingContext.next();
                return;
            }

            String realMethod = routingContext.request().getParam("_method");

            if (realMethod == null || realMethod.trim().isEmpty()) {
                routingContext.next();
                return;
            }

            if (routingContext.request().method() == HttpMethod.POST) {

                HttpMethod putOrDelete = null;

                if (realMethod.toLowerCase().equals("delete")) putOrDelete = HttpMethod.DELETE;
                if (realMethod.toLowerCase().equals("put")) putOrDelete = HttpMethod.PUT;

                if (putOrDelete != null) {
                    routingContext.reroute(putOrDelete, routingContext.normalisedPath());
                    return;
                }

            }

            routingContext.next();

        });

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
