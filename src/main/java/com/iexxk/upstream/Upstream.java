package com.iexxk.upstream;

import com.iexxk.util.RedisClientFactory;
import io.lettuce.core.api.StatefulRedisConnection;

import java.util.concurrent.CopyOnWriteArrayList;

public class Upstream {
    private final String host;
    private final int port;
    private final boolean sentinelMode;
    private final String masterName; // sentinel 主节点名
    private StatefulRedisConnection<String, String> connection;

    private final CopyOnWriteArrayList<Runnable> masterSwitchHandlers = new CopyOnWriteArrayList<>();

    public Upstream(String host, int port, boolean sentinelMode) {
        this(host, port, sentinelMode, null);
    }

    public Upstream(String host, int port, boolean sentinelMode, String masterName) {
        this.host = host;
        this.port = port;
        this.sentinelMode = sentinelMode;
        this.masterName = masterName;
        this.connection = RedisClientFactory.createConnection(host, port, sentinelMode, masterName);
    }

    public StatefulRedisConnection<String, String> getConnection() {
        return connection;
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
    public boolean isSentinel() { return sentinelMode; }
    public String getMasterName() { return masterName; }

    public void registerMasterSwitchHandler(Runnable handler) {
        masterSwitchHandlers.add(handler);
    }

    public void notifyMasterSwitch() {
        for (Runnable r : masterSwitchHandlers) {
            r.run();
        }
    }

    public void switchMaster(StatefulRedisConnection<String, String> newConnection) {
        if (connection != null) {
            connection.close();
        }
        connection = newConnection;
        notifyMasterSwitch();
    }
}