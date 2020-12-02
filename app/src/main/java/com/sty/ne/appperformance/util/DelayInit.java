package com.sty.ne.appperformance.util;

import android.os.Looper;
import android.os.MessageQueue;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/1 10:43 PM
 */
public class DelayInit {
    private Queue<Runnable> delayQueue = new LinkedList<>();

    public void add(Runnable runnable) {
        delayQueue.add(runnable);
    }

    public void start() {
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                Runnable poll = delayQueue.poll();
                if(poll != null) {
                    poll.run();
                }
                return !delayQueue.isEmpty();
            }
        });
    }
}
