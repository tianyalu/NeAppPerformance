package com.sty.ne.appperformance.adapter;

import android.view.ViewGroup;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 8:54 PM
 */
public abstract class BaseDelegate<T> {
    final BaseViewHolder createViewHolder(BaseAdapter<T> adapter, ViewGroup parent, int viewType) {
        BaseViewHolder vh = onCreateViewHolder(parent, viewType);
        if (vh != null) {
            vh.adapter = adapter;
        }
        return vh;
    }

    /**
     * crate view holder by view type
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * get view type by data
     *
     * @param data
     * @param pos
     * @return
     */
    public abstract int getItemViewType(T data, int pos);

    public void onDataSetChanged() {

    }
}
