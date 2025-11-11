package com.iexxk;

import com.iexxk.listener.PortListener;
import com.iexxk.upstream.Upstream;

public class RedisGatewayApplication {
    public static void main(String[] args) {
        // 示例：启动两个端口，分别对应不同 Redis 实例
        Upstream redis1 = new Upstream("127.0.0.1", 6379, false);
        Upstream redis2 = new Upstream("127.0.0.1", 26379, true, "mymaster");

        new PortListener(7000, redis1).start();
        new PortListener(7001, redis2).start();

        System.out.println("Redis Gateway started on ports 7000, 7001");
    }
}