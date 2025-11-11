package com.iexxk.upstream;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MasterSwitchEventBus {
    private static final Map<Upstream, Runnable> handlers = new ConcurrentHashMap<>();

    public static void register(Upstream upstream, Runnable handler) {
        handlers.put(upstream, handler);
    }

    public static void notifyMasterSwitch(Upstream upstream) {
        Runnable handler = handlers.get(upstream);
        if (handler != null) {
            handler.run();
        }
    }
}