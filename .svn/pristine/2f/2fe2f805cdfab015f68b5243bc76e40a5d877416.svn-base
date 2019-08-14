package com.huanhong.content.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.huanhong.content.R;
import com.huanhong.content.util.NormalUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/14.
 */
public class PopAdapter extends BaseAdapter {
    private List<String> list=new ArrayList<>();
    private Map<Integer,Boolean> map=new HashMap<>();
    private OnDeleteClickListener clickListener;
    public PopAdapter(OnDeleteClickListener clickListener){
        map.clear();
        this.clickListener=clickListener;
    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        map.clear();
    }
    public List<String> getList() {
        return list;
    }
    public void setList(List<String> list) {
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView= LayoutInflater.from(NormalUtil.getApplicationContext()).inflate(R.layout.item_pop_spinner_layout,null);
            viewHolder = new ViewHolder();
            viewHolder.tv= (TextView) convertView.findViewById(R.id.item_pop_spinner_tv);
            viewHolder.item_pop_spinner_iv_close= (ImageView) convertView.findViewById(R.id.item_pop_spinner_iv_close);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText(list.get(position));
        viewHolder.item_pop_spinner_iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(position);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView tv;
        ImageView item_pop_spinner_iv_close;
    }

   public interface OnDeleteClickListener{
       void onClick(int position);
   }
}