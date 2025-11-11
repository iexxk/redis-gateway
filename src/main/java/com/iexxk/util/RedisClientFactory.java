package com.iexxk.util;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;

public class RedisClientFactory {

    public static StatefulRedisConnection<String, String> createConnection(
            String host, int port, boolean sentinelMode, String masterName) {
        RedisClient client;
        if (sentinelMode) {
            RedisURI uri = RedisURI.Builder.sentinel(host, port, masterName).build();
            client = RedisClient.create(uri);
        } else {
            RedisURI uri = RedisURI.Builder.redis(host, port).build();
            client = RedisClient.create(uri);
        }
        return client.connect();
    }
}