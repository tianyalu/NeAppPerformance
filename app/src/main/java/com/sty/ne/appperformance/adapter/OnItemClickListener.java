package com.sty.ne.appperformance.adapter;

import android.view.View;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 8:55 PM
 */
public interface OnItemClickListener<T> {
    void onClick(View v, int pos, T data);

    boolean onLongClick(View v, int pos, T data);
}
