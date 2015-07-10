package com.jiang.musicplayer.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by wuliao on 2015/7/9 0009.
 */
//万能adapter适配器
public class ViewHolder {
    public static <T extends View> T findViewById(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
