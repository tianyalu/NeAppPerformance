package com.sty.ne.appperformance.util;

import android.view.Choreographer;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/2 9:11 PM
 */
public class FpsUtil {
    private static long startTime;

    private static int count = 0;

    private static final long INTERVAL = 160 * 1000 * 1000; //160ms

    private static Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if(startTime == 0) {
                startTime = frameTimeNanos; //纳秒
            }
            long interval = frameTimeNanos - startTime;
            if(interval > INTERVAL) {
                double fps = (((double) (count * 1000 * 1000)) / interval) * 1000L; // fps/1000L = (count * 1000 * 1000) / INTERVAL
                LogUtil.d("FPS", "fps=" + fps);
                startTime = 0;
                count = 0;
            }else {
                count++;
            }
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    public static void getFps() {
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    public static void stopGetFps() {
        Choreographer.getInstance().removeFrameCallback(frameCallback);
    }
}
