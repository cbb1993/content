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
import com.zyn.lib.app.AppUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/14.
 */
public class FileAdapter extends BaseAdapter {
    private List<File> list=new ArrayList<>();
    private boolean selectFlag=false;
    private Map<Integer,Boolean> map=new HashMap<>();
    public FileAdapter(){
        map.clear();
    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        map.clear();
    }

    public void setSelectFlag(boolean selectFlag) {
        this.selectFlag = selectFlag;
    }

    public void setList(List<File> list) {
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
            convertView= LayoutInflater.from(AppUtils.getBaseApplication()).inflate(R.layout.item_file_layout,null);
            viewHolder = new ViewHolder();
            viewHolder.tv= (TextView) convertView.findViewById(R.id.item_file_tv_name);
            viewHolder.checkbox_select= (CheckBox) convertView.findViewById(R.id.item_file_checkbox_select);
            viewHolder.iv_logo= (ImageView) convertView.findViewById(R.id.item_file_iv_logo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        File file=list.get(position);
        if(file!=null){
            if(file.isFile()){
                String[] vedios=AppUtils.getBaseApplication().getResources().getStringArray(R.array.fileEndingVideo);
                String[] pics=AppUtils.getBaseApplication().getResources().getStringArray(R.array.fileEndingImage);
                for(String s:vedios){
                    if(file.getPath().endsWith(s)){
                        viewHolder.iv_logo.setImageResource(R.drawable.shipin);
                    }
                }
                for(String s:pics){
                    if(file.getPath().toLowerCase().endsWith(s)){
                        viewHolder.iv_logo.setImageResource(R.drawable.tupian);
                    }
                }
            }else  {
                viewHolder.iv_logo.setImageResource(R.mipmap.folder);
            }
            viewHolder.tv.setText(file.getName());
            viewHolder.checkbox_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    map.put(position,isChecked);
                }
            });
            if(selectFlag){
                if(map.keySet().contains(position)){
                    viewHolder.checkbox_select.setChecked(map.get(position));
                }else   viewHolder.checkbox_select.setChecked(false);

                viewHolder.checkbox_select.setVisibility(View.VISIBLE);
            }else {
                viewHolder.checkbox_select.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView tv;
        CheckBox checkbox_select;
        ImageView iv_logo;
    }
}