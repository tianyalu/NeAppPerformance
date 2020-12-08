package com.sty.ne.appperformance.thread;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/8 9:27 PM
 */
public class HandlerThreadUtils {
    private static final Map<String, HandlerThread> threads = new HashMap<>();

    public static HandlerThread newHandlerThread(String name) {
        HandlerThread handlerThread;
        synchronized (threads) {
            handlerThread = threads.get(name);
            if(handlerThread != null && handlerThread.getLooper() == null) {
                threads.remove(name);
                handlerThread = null;
            }
            if(handlerThread == null) {
                handlerThread = new HandlerThread(name);
                handlerThread.start();
                threads.put(name, handlerThread);
            }
        }
        return handlerThread;
    }

    public final Handler newHandler(String name) {
        return new Handler(newHandlerThread(name).getLooper());
    }
}
