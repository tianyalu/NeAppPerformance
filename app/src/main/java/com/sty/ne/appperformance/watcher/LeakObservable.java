package com.sty.ne.appperformance.watcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/7 9:29 PM
 */
public class LeakObservable implements LeakObserver{
    private static volatile LeakObservable instance;
    private List<LeakObserver> list;
    private LeakObservable() {
        list = new ArrayList<>();
    }

    public static LeakObservable getInstance() {
        if(instance == null) {
            synchronized (LeakObservable.class) {
                if(instance == null) {
                    instance = new LeakObservable();
                }
            }
        }
        return instance;
    }

    public void register(LeakObserver observer) {
        list.add(observer);
    }

    public void unRegister(LeakObserver observer) {
        list.remove(observer);
    }

    @Override
    public void handle() {
        //TODO
    }
}
