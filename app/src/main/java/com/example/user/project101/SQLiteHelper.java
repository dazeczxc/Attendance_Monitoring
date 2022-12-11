package com.example.user.project101;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by USER on 09/07/2021.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public  void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    //insert data

    public void insertData(String subject ){
        SQLiteDatabase database = getWritableDatabase();
        //query to insert into database
        String sql = "INSERT INTO RECORD VALUES (NULL, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, subject);

        statement.executeInsert();
    }

    //update data
    public void updateData(String subject, int id){
        SQLiteDatabase database = getWritableDatabase();
        //query to update record
        String sql = "UPDATE RECORD SET subject=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, subject);
        statement.bindDouble(2, (double)id);

        statement.executeInsert();
        database.close();

    }

    //delete data
    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM RECORD WHERE id=?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double) id);

        statement.execute();
        database.close();

    }

    //get data
    public Cursor getData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
