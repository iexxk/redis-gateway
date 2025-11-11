package com.iexxk.util;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisCredentials;
import io.lettuce.core.RedisCredentialsProvider;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;

public class RedisClientFactory {

    public static StatefulRedisConnection<String, String> createConnection(
            String host, int port, boolean sentinelMode, String masterName,String username, String password) {
        RedisClient client;
        RedisCredentialsProvider credentialsProvider =
                RedisCredentialsProvider.from(() -> RedisCredentials.just(username, password.toCharArray()));
        if (sentinelMode) {
            RedisURI uri = RedisURI.Builder.sentinel(host, port, masterName)
                    .withSentinelMasterId(masterName)
                    .withAuthentication(credentialsProvider)
                    .build();
            uri.getSentinels().forEach((sentinel) -> {
                sentinel.setCredentialsProvider(credentialsProvider);
            });

            client = RedisClient.create(uri);
        } else {
            RedisURI uri = RedisURI.Builder
                    .redis(host, port)
                    .withAuthentication(credentialsProvider)
                    .build();
            client = RedisClient.create(uri);
        }
        return client.connect();
    }
}