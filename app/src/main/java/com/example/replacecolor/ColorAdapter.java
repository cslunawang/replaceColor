package com.example.replacecolor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ColorAdapter extends BaseAdapter {
    public List<Map.Entry<String,Integer>> list;
    public Map<String,String> map;
    Context context;


    public ColorAdapter(Context context,List<Map.Entry<String, Integer>> list) {
        this.context = context;
        this.list = list;
    }

    private class ViewHolder{
        TextView oldBg;
        TextView newBg;
        Button oldText_Bt;
        TextView newText;
        TextView count;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(null == view){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view  = inflater.inflate(R.layout.color_item,null);
            viewHolder.oldBg = (TextView) view.findViewById(R.id.old_bg);
            viewHolder.newBg = (TextView) view.findViewById(R.id.new_bg);
            viewHolder.oldText_Bt = (Button) view.findViewById(R.id.old_bt);
            viewHolder.newText = (TextView) view.findViewById(R.id.new_text);
            viewHolder.count = (TextView) view.findViewById(R.id.count);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = ( ViewHolder) view.getTag();
        }
        if(null != list){
            Map.Entry<String,Integer> entry = list.get(i);
            viewHolder.count.setText("出现了："+ entry.getValue() +" 次");
            viewHolder.oldBg.setBackgroundColor(Color.parseColor(entry.getKey()));
            viewHolder.oldText_Bt.setText(entry.getKey());
            viewHolder.newText.setText(entry.getKey());
            viewHolder.newBg.setBackgroundColor(Color.parseColor(entry.getKey()));
        }
        return view;
    }

}

