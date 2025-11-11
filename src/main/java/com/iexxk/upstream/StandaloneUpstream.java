package com.iexxk.upstream;

public class StandaloneUpstream implements Upstream {

    private final HostPort fixed;

    public StandaloneUpstream(String host, int port) {
        this.fixed = new HostPort(host, port);
    }

    @Override
    public HostPort getCurrentMaster() {
        return fixed;
    }
}
