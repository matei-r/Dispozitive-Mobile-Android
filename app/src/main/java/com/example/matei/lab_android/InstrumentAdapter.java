package com.example.matei.lab_android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;

/**
 * Created by Matei on 12/1/2016.
 */

class Instrument {
    public Instrument(int id,String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    private int id;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String type;

}

public class InstrumentAdapter extends ArrayAdapter<Instrument> {

    private ViewHolder viewHolder;

    public InstrumentAdapter(UserProfileActivity userProfileActivity, int list_item, ArrayList<Instrument> instrumentList) {
        super(userProfileActivity,list_item,instrumentList);
    }

    private static class ViewHolder {
        private CheckedTextView itemView;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.itemView = (CheckedTextView) convertView.findViewById(R.id.checkTextView1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Instrument item = getItem(position);
        if(item != null){
            viewHolder.itemView.setText(String.format("Name : %s Type : %s",item.getName(),item.getType()));
        }

        return convertView;
    }
}
