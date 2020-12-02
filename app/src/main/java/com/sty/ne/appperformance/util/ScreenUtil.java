package com.sty.ne.appperformance.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * @Author: tian
 * @UpdateDate: 2020/11/30 8:47 PM
 */
public class ScreenUtil {

    private static final String TAG = "ScreenUtil";

    private static double RATIO = 0.85;

    public static int screenWidth;

    public static int screenHeight;

    public static int screenMin;// 宽高中，较小的值

    public static int screenMax;// 宽高中，较大的值

    public static float density;

    public static float scaleDensity;

    public static float xdpi;

    public static float ydpi;

    public static int densityDpi;

    public static int dialogWidth;

    public static int statusbarheight;

    public static int navbarheight;

    public static float actionbarheight;

    public static void init(Context context) {
        if (null == context) {
            return;
        }
        context = context.getApplicationContext();
        statusbarheight = getStatusBarHeight(context);
        navbarheight = getNavBarHeight(context);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getRealMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = hasNaviBar(windowMgr) ? dm.heightPixels - navbarheight : dm.heightPixels;
        screenMin = (screenWidth > screenHeight) ? screenHeight : screenWidth;
        screenMax = (screenWidth < screenHeight) ? screenHeight : screenWidth;
        density = dm.density;
        scaleDensity = dm.scaledDensity;
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;
        densityDpi = dm.densityDpi;
        Log.d(TAG, "screenWidth=" + screenWidth + " screenHeight=" + screenHeight + " density=" + density);
    }

    public static int getStatusBarHeight(Context context) {
        if (statusbarheight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusbarheight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (statusbarheight == 0) {
            statusbarheight = ScreenUtil.dip2px(context, 25);
        }
        return statusbarheight;
    }


    private static boolean hasNaviBar(WindowManager windowManager) {
        Display d = windowManager.getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }


    public static int getStatusBarHeightActivity(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int height = rect.top;
        if (height == 0) {
            height = getStatusBarHeight(activity);
        }
        return height;
    }

    public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = ScreenUtil.getDisplayDensity(context);
        return (int) (dipValue * scale + 0.5f);
    }

    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = ScreenUtil.getDisplayDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }

    public static float sp2px(Context context, float spValue) {
        final float fontScale = ScreenUtil.getScaleDensity(context);
        return (spValue * fontScale + 0.5f);
    }

    public static float px2sp(Context context, float pxValue) {
        final float fontScale = ScreenUtil.getScaleDensity(context);
        return (pxValue / fontScale + 0.5f);
    }


    private static float getScaleDensity(Context context) {
        if (scaleDensity == 0) {
            init(context);
        }
        return scaleDensity;
    }

    private static float getDisplayDensity(Context context) {
        if (density == 0) {
            init(context);
        }
        return density;
    }

    public static int getDisplayWidth(Context context) {
        if (screenWidth == 0) {
            init(context);
        }
        return screenWidth;
    }

    public static int getDisplayHeight(Context context) {
        //
        init(context);
        return screenHeight;
    }

    public static int getScreenMin(Context context) {
        if (screenMin == 0) {
            init(context);
        }
        return screenMin;
    }

    public static int getScreenMax(Context context) {
        if (screenMin == 0) {
            init(context);
        }
        return screenMax;
    }

    public static int getDialogWidth(Context context) {
        dialogWidth = (int) (getScreenMin(context) * RATIO);
        return dialogWidth;
    }

    public static float getActionbarHeight(Context context, int action_bar_height) {
        if (actionbarheight == 0) {
            actionbarheight = context.getResources().
                    getDimension(action_bar_height);
        }
        return actionbarheight;

    }

    public static float getScreenHeight(Context context) {
        float screenHeight = getDisplayHeight(context);
        float screenWidth = getDisplayWidth(context);
        if (screenWidth > screenHeight) {
            screenHeight = screenWidth;
        }
        float statusBarHeight = ScreenUtil.statusbarheight;
        float navHeight = navbarheight;
        String deviceName = android.os.Build.MANUFACTURER;
        if (!TextUtils.isEmpty(deviceName) && deviceName.toLowerCase().contains("meizu")) {
            int sbAutoHide = Settings.System.getInt(context.getContentResolver(), "mz_smartbar_auto_hide", 0);
            if (sbAutoHide == 1) {
                return screenHeight - statusBarHeight;
            }
            return screenHeight - statusBarHeight - navHeight;
        }
        return screenHeight - statusBarHeight;
    }

}
