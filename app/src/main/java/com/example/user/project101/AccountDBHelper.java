package com.example.user.project101;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by USER on 16/07/2021.
 */
public class AccountDBHelper extends SQLiteOpenHelper {
    private Object myDb;
    public AccountDBHelper(Context context) {

        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase myDb) {

        myDb.execSQL("create Table if not exists tbusers(username Text primary key, password Text, name Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase myDb, int oldVersion, int newVersion) {

    }

    public boolean insertData (String strusername, String strpassword, String strname)
    {
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", strusername);
        contentValues.put("password", strpassword);
        contentValues.put("name", strname);

        long result = myDb.insert("tbusers", null, contentValues);

        if (result == -1) //if the user is already existing
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean checkusername (String strusername)
    {
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor cursor = myDb.rawQuery("select * from tbusers where username =?", new String[]{strusername});
        if (cursor.getCount()>0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean checkusernamepassword (String strusername, String strpassword)
    {
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor cursor = myDb.rawQuery("select * from tbusers where username =? and password =?", new String[]{strusername, strpassword});
        if (cursor.getCount()>0)
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        return database.rawQuery(sql, null);
    }

}
