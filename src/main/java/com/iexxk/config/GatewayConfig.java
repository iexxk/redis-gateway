package com.iexxk.config;

import java.util.List;

public class GatewayConfig {
    public static class PortConfig {
        public int port;
        public String type; // sentinel | standalone
        public String masterName;
        public List<String> sentinels;
        public String host;
        public int hostPort;
    }

    public List<PortConfig> ports;
}
