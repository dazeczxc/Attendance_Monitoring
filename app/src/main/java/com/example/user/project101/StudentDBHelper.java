package com.example.user.project101;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by dasec on 24/11/2021.
 */
public class StudentDBHelper extends SQLiteHelper {

    SQLiteDatabase studentDB;

    private static final String DBNAME = "studentDB.db";
    private static final int VERSION = 1;

    public static final String TABLE_NAME = "Student";
    public static final String ID = "studentID";
    public static final String NAME = "name";
    public static final String SUBJECT = "subject";

    public StudentDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT NOT NULL, " +
                SUBJECT + " TEXT NOT NULL " +

                ")";

        db.execSQL(queryTable);
    }

    public  void openDB(){
        studentDB = getWritableDatabase();
    }

    public void closeDB(){
        if(studentDB != null && studentDB.isOpen()){
            studentDB.close();
        }
    }


    public long insert(String name, String subject){
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(SUBJECT, subject);


        return studentDB.insert(TABLE_NAME, null, values);
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

    //delete
    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM "+ TABLE_NAME + " WHERE studentID = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);
        statement.execute();
    }

}
