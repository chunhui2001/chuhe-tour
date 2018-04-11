package com.shenmao.chuhe.queue;

import com.shenmao.chuhe.redis.RedisStore;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.Session;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

public class RedisQueueImpl implements RedisQueue {

    private static final Logger logger = LoggerFactory.getLogger(RedisQueueImpl.class);

    private static String DEFAULT_MESSAGE_MAP_NAME = "vertx-web.messages";

    private final Vertx vertx;
    private final int retryTimeout;
    private final LocalMap<String, Session> localMap;

    //默认值
    private String host = "localhost";
    private int port = 6379;
    private String auth;

    RedisClient redisClient;


    public RedisQueueImpl(Vertx vertx, int retryTimeout) {

        this.vertx = vertx;
        this.retryTimeout = retryTimeout;

        localMap = vertx.sharedData().getLocalMap(DEFAULT_MESSAGE_MAP_NAME);

    }

    @Override
    public RedisStore init() {
        this.redisManager();
        return this;
    }

    private void redisManager() {
        RedisOptions redisOptions = new RedisOptions();
        redisOptions.setHost(host).setPort(port).setAuth(auth);

        redisClient = RedisClient.create(vertx, redisOptions);
    }

    @Override
    public RedisStore host(String host) {
        this.host = host;
        return this;
    }

    @Override
    public RedisStore port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public RedisStore auth(String pwd) {
        this.auth = pwd;
        return this;
    }

    @Override
    public void publish(String message) {

        System.out.println(message);
    }

    @Override
    public void subscribe() {

    }
}
