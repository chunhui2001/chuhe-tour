package com.shenmao.chuhe.queue;

import com.shenmao.chuhe.redis.RedisStore;

public interface RedisQueue extends RedisStore {

    void publish(String message);
    void subscribe();
}
