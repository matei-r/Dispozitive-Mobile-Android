package com.example.matei.lab_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Matei on 1/2/2017.
 */

public class DBController extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "InstrumentDB";

    private static final String TABLE_USERS = "users";
    private static final String TABLE_INSTRUMENTS = "instruments";
    private static final String TABLE_QUERYS = "querys";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private static final String KEY_TYPE = "type";
    private static final String KEY_USER_ID = "user_id";

    private static final String KEY_QUERY = "query";

    public DBController(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addQuery(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUERY,query);
        db.insert(TABLE_QUERYS,null,values);
        db.close();
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<User>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do {
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setUsername(cursor.getString(3));
                user.setPassword(cursor.getString(4));
                users.add(user);
            } while (cursor.moveToNext());
        }
        return users;
    }

    public void addUser(User user,boolean connected) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,user.getName());
        values.put(KEY_EMAIL,user.getEmail());
        values.put(KEY_USERNAME,user.getUsername());
        values.put(KEY_PASSWORD,user.getPassword());
        db.insert(TABLE_USERS,null,values);
        if(connected == false) {
            String query = "INSERT INTO " + TABLE_USERS + " (name,email,username,password)" + " VALUES (\'" + user.getName() + "\',\'" + user.getEmail() + "\',\'" + user.getUsername() + "\',\'" + user.getPassword() + "\')";
            addQuery(query);
        }
        db.close();

    }

    public User findUser(String username,String password){

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,new String[] { username,password});

        User user = new User();

        if(cursor.moveToFirst()) {
            cursor.moveToFirst();
            user.setId(Integer.parseInt(cursor.getString(0)));
            user.setName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setUsername(cursor.getString(3));
            user.setPassword(cursor.getString(4));
        } else {
            user = null;
        }
        db.close();
        return user;
    }

    public void deleteTableContent(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName);
    }

    public void addInstrument(Instrument instrument,boolean connected) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,instrument.getName());
        values.put(KEY_TYPE,instrument.getType());
        values.put(KEY_USER_ID,instrument.getId());
        db.insert(TABLE_INSTRUMENTS,null,values);
        if(connected == false) {
            String query = "INSERT INTO " + TABLE_INSTRUMENTS + " (name,type,user_id)" + " VALUES (\'" + instrument.getName() + "\',\'" + instrument.getType() + "\'," + instrument.getId() + ")";
            addQuery(query);
        }
        db.close();

    }

    public void deleteInstrument(String ids,boolean connected) {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] delete_ids = ids.split(";");
        int i;
        for(i=0;i<delete_ids.length;i++){
            db.delete(TABLE_INSTRUMENTS,KEY_ID + " = ?",new String[] { delete_ids[i]});
            if(connected == false) {
                String query = "DELETE FROM " + TABLE_INSTRUMENTS + " WHERE id = " + delete_ids[i];
                addQuery(query);
            }
        }
        db.close();

    }

    public void updateInstrument(Instrument instrument,boolean connected){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int id = instrument.getId();
        String name = instrument.getName();
        String type = instrument.getType();
        values.put(KEY_NAME,name);
        values.put(KEY_TYPE,type);
        db.update(TABLE_INSTRUMENTS,values,KEY_ID + " = ? ",new String[] { String.valueOf(id)});
        if(connected == false) {
            String query = "UPDATE " + TABLE_INSTRUMENTS + " SET name = \'" + name + "\' , type = \'" + type + "\' WHERE id = " + String.valueOf(id);
            addQuery(query);
        }

    }

    public ArrayList<String> getAllQuerys() {
        ArrayList<String> querys = new ArrayList<String>();
        String selectQuery = "SELECT * FROM " + TABLE_QUERYS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do {
                querys.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return querys;
    }

    public ArrayList<Instrument> getAllInstruments(String id) {

        ArrayList<Instrument> instruments = new ArrayList<Instrument>();
        String query = "SELECT * FROM " + TABLE_INSTRUMENTS + " WHERE user_id = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,new String[] { id});
        if(cursor.moveToFirst()){
            do {
                Instrument instrument = new Instrument(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
                instruments.add(instrument);
            } while (cursor.moveToNext());
        }
        return instruments;
    }

    public void refreashTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUERYS);
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME +
                " TEXT," + KEY_EMAIL + " TEXT," + KEY_USERNAME + " TEXT," + KEY_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);
        String CREATE_INSTRUMENT_TABLE = "CREATE TABLE " + TABLE_INSTRUMENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME +
                " TEXT," + KEY_TYPE + " TEXT," + KEY_USER_ID + " INTEGER" + ")";
        db.execSQL(CREATE_INSTRUMENT_TABLE);
        String CREATE_QUERYS_TABLE = "CREATE TABLE " + TABLE_QUERYS + "(" + KEY_QUERY + " TEXT" + ")";
        db.execSQL(CREATE_QUERYS_TABLE);
    }
}
