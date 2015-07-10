package com.jiang.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/7/9 0009.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter{
    public List<T> list;
    public LayoutInflater mInflate;
    public Context context;
    public MyBaseAdapter(Context context,List<T> list){
        this.list=list;
        this.context=context;
        mInflate=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItemView(position,convertView,parent);
    }
    public abstract View getItemView(int position, View convertView, ViewGroup parent);
}
