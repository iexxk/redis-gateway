package com.iexxk.sentinel;

import com.iexxk.events.MasterSwitchEventBus;
import com.iexxk.upstream.HostPort;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;


import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LettuceSentinelMonitor {

    private final String masterName;
    private final List<String> sentinelAddrs;
    private final AtomicReference<HostPort> masterRef;

    public LettuceSentinelMonitor(String masterName,
                                  List<String> sentinelAddrs,
                                  AtomicReference<HostPort> masterRef) {
        this.masterName = masterName;
        this.sentinelAddrs = sentinelAddrs;
        this.masterRef = masterRef;
    }

    public void start() {
        new Thread(this::monitorLoop, "sentinel-monitor-" + masterName).start();
    }

    private void monitorLoop() {
        try {
            // 构造 RedisURI
            RedisURI.Builder builder = RedisURI.Builder.sentinel(masterName);
            for (String s : sentinelAddrs) {
                String[] hp = s.split(":");
                builder.withSentinel(hp[0], Integer.parseInt(hp[1]));
            }
            RedisURI uri = builder.build();

            RedisClient client = RedisClient.create(uri);

            StatefulRedisSentinelConnection<String, String> conn = client.connectSentinel();

            // 获取 master
            updateMaster(conn);

            // 监听 +switch-master 事件
            conn.addListener((event) -> {
                if (event.getClass().getSimpleName().equals("SwitchMasterEvent")) {
                    updateMaster(conn);
                    MasterSwitchEventBus.notify(masterName);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMaster(StatefulRedisSentinelConnection<String, String> conn) {
        var master = conn.sync().getMasterAddrByName(masterName);
        masterRef.set(new HostPort(master.getIp(), master.getPort()));
    }
}

