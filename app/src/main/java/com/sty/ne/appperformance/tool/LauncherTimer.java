package com.sty.ne.appperformance.tool;

import com.sty.ne.appperformance.util.LogUtil;

/**
 * @Author: tian
 * @UpdateDate: 2020/11/30 9:50 PM
 */
public class LauncherTimer {
    public static long startTime;

    public static void logStart() {
        startTime = System.currentTimeMillis();
    }

    public static void logEnd(String tag) {
        LogUtil.d("Time", tag + " launcher time=" + (System.currentTimeMillis() - startTime));
    }
}
