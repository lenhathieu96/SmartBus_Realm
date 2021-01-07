package com.smartbus_realm.GPS;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public void insertDataLocalStorage(String key, String value){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO catalystLocalStorage VALUES (?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, key);
        statement.bindString(2, value);
        statement.executeInsert();
    }

    public boolean updateDataLocalStorage(String keyName,String valueName){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Key",keyName);
        contentValues.put("Value",valueName);
        database.update("catalystLocalStorage",contentValues,"Key=?",new String[]{keyName});
//        database.insert();

        return true;
    }
    @SuppressLint("Recycle")
    public String selectDataLocalStorage(String keyName){
        SQLiteDatabase database = getWritableDatabase();
        Cursor c = database.rawQuery("Select * from catalystLocalStorage where Key = '"+keyName+"'",null);
        String key = "", value ="";
        if (c.moveToFirst()){
            do {
                key = c.getString(0);
                value = c.getString(1);
                Log.e("DEVK", "Key: "+key+" Value: "+value);
                return key;
            } while(c.moveToNext());
        }else{
            return "";
        }
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) { }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
