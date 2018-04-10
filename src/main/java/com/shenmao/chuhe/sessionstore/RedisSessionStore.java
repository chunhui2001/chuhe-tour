package com.shenmao.chuhe.sessionstore;

import com.shenmao.chuhe.redis.RedisStore;
import io.vertx.core.Vertx;
import io.vertx.ext.web.sstore.SessionStore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface RedisSessionStore extends SessionStore, RedisStore {


}
