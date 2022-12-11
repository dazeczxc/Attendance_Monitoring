package com.example.user.project101;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by USER on 27/06/2021.
 */
public class SubjectDBHelper extends SQLiteOpenHelper {

    SQLiteDatabase myDBHAHA;

    private static final int VERSION = 1;

    public static final String TABLE_SUBJECT = "tblSubject";
    public static final String ID = "id";
    public static final String SUBJECT = "subject";
    public static final String SECTION = "section";
    public static final String DAY = "day";
    public static final String TIME = "time";
    public static final String TIMETO = "timeto";



    public SubjectDBHelper(Context context) {
        super(context, TABLE_SUBJECT, null, VERSION);
    }

    public SubjectDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryTableSubject = "CREATE TABLE IF NOT EXISTS " + TABLE_SUBJECT + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SUBJECT + " TEXT NOT NULL, " +
                SECTION + " TEXT NOT NULL, " +
                DAY + " TEXT NOT NULL, " +
                TIME + " TEXT NOT NULL, " +
                TIMETO + " TEXT NOT NULL " +



                ")";

        db.execSQL(queryTableSubject);
    }

    public  void openDB(){
        myDBHAHA = getWritableDatabase();
    }

    public void closeDB(){
        if(myDBHAHA != null && myDBHAHA.isOpen()){
            myDBHAHA.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //insert
    public long insertSub(String subject, String section, String day, String time, String timeto){
        ContentValues values = new ContentValues();
        values.put(SUBJECT, subject);
        values.put(SECTION, section);
        values.put(DAY, day);
        values.put(TIME, time);
        values.put(TIMETO, timeto);

        return myDBHAHA.insert(TABLE_SUBJECT, null, values);
    }

    //load
    public Cursor getListContentsSubject(){
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor datas = db.rawQuery("SELECT * FROM "+ TABLE_SUBJECT, null);
        return  datas;
    }

    //new update data
    public  void updateData(String subject, String section, String day, String time, String timeto, int id){
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE "+TABLE_SUBJECT+ " SET subject=?, section=?, day=?, time=?, timeto=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, subject);
        statement.bindString(2, section);
        statement.bindString(3, day);
        statement.bindString(4, time);
        statement.bindString(4, timeto);

        statement.bindDouble(5, (double)id);

        statement.execute();
    }

    //delete
    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM "+ TABLE_SUBJECT + " WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);
        statement.execute();
    }

    //getting data
    public Cursor getData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        return database.rawQuery(sql, null);
    }

    //selects specific data
    public void selectSpecificData(int id, String subject, String section, String day){
        SQLiteDatabase database= this.getWritableDatabase();
        String sql = "SELECT * FROM "+ TABLE_SUBJECT + " WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, subject);
        statement.bindString(2, section);
        statement.bindString(3, day);
        statement.bindDouble(4, (double)id);

        statement.execute();
    }

}
