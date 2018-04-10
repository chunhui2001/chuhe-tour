package com.shenmao.chuhe.sessionstore;

import com.shenmao.chuhe.redis.RedisStore;
import io.vertx.core.Vertx;
import io.vertx.ext.web.sstore.SessionStore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface RedisSessionStore extends SessionStore, RedisStore {

    static RedisStore create(String className, Vertx vertx) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor = clazz.getConstructor(Vertx.class, int.class);

        return (RedisStore)ctor.newInstance(new Object[] { vertx, DEFAULT_RETRY_TIMEOUT });

        // return new RedisSessionStoreImpl(vertx, DEFAULT_RETRY_TIMEOUT);
    }

    static RedisStore create(String className, Vertx vertx, int reaperInterval) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor = clazz.getConstructor(Vertx.class, int.class);

        return (RedisStore)ctor.newInstance(new Object[] { vertx, reaperInterval });

        // return new RedisSessionStoreImpl(vertx, reaperInterval);
    }
}
