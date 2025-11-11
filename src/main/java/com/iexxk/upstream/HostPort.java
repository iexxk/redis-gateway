package com.iexxk.upstream;

public class HostPort {
    public final String host;
    public final int port;

    public HostPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
