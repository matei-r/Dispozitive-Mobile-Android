package com.example.matei.lab_android;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matei on 10/28/2016.
 */

public class TableControllerInstrument extends DatabaseHandler {

    public TableControllerInstrument(Context context){
        super(context);
    }

    public boolean create(Instrument instrument){

        ContentValues values = new ContentValues();

        values.put("name",instrument.name);
        values.put("type",instrument.type);

        SQLiteDatabase db = this.getWritableDatabase();

        boolean createSuccessful = db.insert("instruments",null,values) > 0;
        db.close();

        return createSuccessful;
    }

    public List<Instrument> read(){

        List<Instrument> recordList = new ArrayList<Instrument>();

        String sql = "SELECT * FROM instruments ORDER BY id ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            do {

                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String type = cursor.getString(cursor.getColumnIndex("type"));

                Instrument instrument = new Instrument();
                instrument.id = id;
                instrument.name = name;
                instrument.type = type;

                recordList.add(instrument);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recordList;
    }

    public void delete(int i){

        String sql = "DELETE FROM instruments WHERE id = " + Integer.toString(i);

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);

    }

}
