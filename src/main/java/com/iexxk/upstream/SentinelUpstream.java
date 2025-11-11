package com.iexxk.upstream;

import com.iexxk.sentinel.LettuceSentinelMonitor;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SentinelUpstream implements Upstream {

    private final AtomicReference<HostPort> currentMaster = new AtomicReference<>();

    public SentinelUpstream(String masterName, List<String> sentinels) {
        new LettuceSentinelMonitor(masterName, sentinels, currentMaster).start();
    }

    @Override
    public HostPort getCurrentMaster() {
        return currentMaster.get();
    }
}

