package com.sty.ne.appperformance.widget;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/2 10:18 PM
 */
public class DroidCard {

    public int x;//左侧绘制起点

    public int width;

    public int height;

    public Bitmap bitmap;

    public DroidCard(Resources res, int resId, int x) {
        this.bitmap = BitmapFactory.decodeResource(res, resId);
        this.x = x;
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();
    }
}
