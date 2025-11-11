package com.iexxk.events;

import java.util.ArrayList;
import java.util.List;

public class MasterSwitchEventBus {

    private static final List<Runnable> handlers = new ArrayList<>();

    public static void register(Runnable handler) {
        handlers.add(handler);
    }

    public static void notify(String masterName) {
        for (Runnable r : handlers) {
            r.run();
        }
    }
}
