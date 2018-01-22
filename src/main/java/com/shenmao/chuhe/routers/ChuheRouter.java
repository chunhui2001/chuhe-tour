package com.shenmao.chuhe.routers;

import com.shenmao.chuhe.passport.AuthHandlerImpl;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.AuthHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface ChuheRouter {


    public static ChuheRouter create(String className, Vertx vertx)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor = clazz.getConstructor(Vertx.class, AuthHandler.class);
        ChuheRouter object = (ChuheRouter)ctor.newInstance(new Object[] { vertx, AuthHandlerImpl.create(vertx) });

        return object;
    }

    public Router getRouter() ;


}
