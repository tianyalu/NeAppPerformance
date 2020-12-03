package com.sty.ne.appperformance.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.sty.ne.appperformance.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/2 10:13 PM
 */
public class DroidCardsViewOp extends View {
    //图片与图片之间的间距
    private int mCardSpacing = 50;
    //图片与左侧距离的记录
    private int mCardLeft = 10;
    private List<DroidCard> mDroidCards = new ArrayList<>();
    private Paint paint = new Paint();

    public DroidCardsViewOp(Context context) {
        super(context);
        initCards();
    }

    public DroidCardsViewOp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initCards();
    }

    /**
     * 初始化卡片集合
     */
    private void initCards() {
        Resources res = getResources();
        mDroidCards.add(new DroidCard(res, R.drawable.alex, mCardLeft));
        mCardLeft += mCardSpacing;
        mDroidCards.add(new DroidCard(res, R.drawable.claire, mCardLeft));
        mCardLeft += mCardSpacing;
        mDroidCards.add(new DroidCard(res, R.drawable.kathryn, mCardLeft));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mDroidCards.size() - 1; i++) {
            drawDroidCard(canvas, mDroidCards, i);
        }
        drawLastDroidCard(canvas, mDroidCards.get(mDroidCards.size() - 1));
        invalidate();
    }

    /**
     * 绘制最后一个DroidCard
     * @param canvas
     * @param card
     */
    private void drawLastDroidCard(Canvas canvas, DroidCard card) {
        canvas.drawBitmap(card.bitmap, card.x, 0f, paint);
    }

    /**
     * 绘制DroidCard
     * @param canvas
     * @param mDroidCards
     * @param i
     */
    private void drawDroidCard(Canvas canvas, List<DroidCard> mDroidCards, int i) {
        DroidCard card = mDroidCards.get(i);
        canvas.save(); //画布保存
        canvas.clipRect(card.x, 0f, (mDroidCards.get(i + 1).x), card.height); //关键计算画布大小
        canvas.drawBitmap(card.bitmap, card.x, 0f, paint);
        canvas.restore(); //画布裁剪
    }
}
