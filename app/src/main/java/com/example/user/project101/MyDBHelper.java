package com.example.user.project101;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by USER on 26/06/2021.
 */
public class MyDBHelper extends SQLiteOpenHelper{

    SQLiteDatabase myDB;

    private static final String DBNAME = "mydb.db";
    private static final int VERSION = 1;

    public static final String TABLE_NAME = "Attendance";
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String SUBJECT = "subject";

    public MyDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                                NAME + " TEXT NOT NULL, " +
                                TIME + " TEXT NOT NULL, " +
                                DATE + " TEXT NOT NULL, " +
                                SUBJECT + " TEXT NOT NULL " +
                ")";

        db.execSQL(queryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public  void openDB(){
        myDB = getWritableDatabase();
    }

    public void closeDB(){
        if(myDB != null && myDB.isOpen()){
            myDB.close();
        }
    }

    public long insert(String name, String date, String time, String subject){
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(TIME, time);
        values.put(DATE, date);
        values.put(SUBJECT, subject);

        return myDB.insert(TABLE_NAME, null, values);
    }

    public Cursor getListContents(){
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+ TABLE_NAME, null);
        return  data;
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        return database.rawQuery(sql,null);
    }
}
