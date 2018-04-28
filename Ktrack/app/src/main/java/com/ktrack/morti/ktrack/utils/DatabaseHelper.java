package com.ktrack.morti.ktrack.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by User on 2/28/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "ktrack_contacts";
    private static final String COL1 = "ID";
    private static final String COL2 = "name";
    private static final String COL3 = "phone";
    private static final String COL4 = "mainContact";



    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL2 +" TEXT, "
                + COL3 +" TEXT, "
                + COL4 +" TEXT );";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String name,String phone, String mainContact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, name);
        contentValues.put(COL3, phone);
        contentValues.put(COL4, mainContact);

        Log.d(TAG, "addData: Adding " + name + " to " + TABLE_NAME);


        if(userExist(name)){
            return false;
        }
        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT *" + " FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public boolean userExist(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1));
            Log.e(TAG,data.getString(1));
        }
        if (listData.size()<1){
            return false;
        }
        data.close();
        return true;
    }

    public boolean primaryContactExists(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT *" + " FROM " + TABLE_NAME +
                " WHERE " + COL4 + " = 'Y'";
        Cursor data = db.rawQuery(query, null);
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1));
            Log.e(TAG,data.getString(1));
        }
        if (listData.size()<1){
            return false;
        }
        data.close();
        return true;
    }

    public void endPrimary(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL4 +
                " = 'N' WHERE " + COL4 + " = 'Y'";
        db.execSQL(query);
    }

    public Cursor getPrimaryContact(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT *" +  " FROM " + TABLE_NAME +
                " WHERE " + COL4 + " = 'Y'";
        return db.rawQuery(query, null);
        //TODO return the whole user object
    }

    /**
     * Delete from database
     * @param id
     * @param name
     *
    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + " from database.");
        db.execSQL(query);
    }*/


}
