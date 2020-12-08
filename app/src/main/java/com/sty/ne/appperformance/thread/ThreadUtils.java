package com.sty.ne.appperformance.thread;

import android.text.TextUtils;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/8 9:19 PM
 */
public class ThreadUtils {
    private static int asyncIndex = 0;

    public static Thread newThread(Runnable runnable, String name) {
        if(TextUtils.isEmpty(name)) {
            name = buildName();
        }
        return new Thread(runnable, name);
    }

    private static String buildName() {
        return "async" + asyncIndex++;
    }
}
