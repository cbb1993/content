package com.zyn.lib.view.recycleview;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tuka2401 on 2016/10/9.
 */
public interface OnItemClickListener<T>
{
    void onItemClick(ViewGroup parent, View view, T t, int position);
    boolean onItemLongClick(ViewGroup parent, View view, T t, int position);
}
