package com.chenzj.myledger.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.chenzj.myledger.utils.TimeUtils;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/2/20 20:22
 */
public class CacheDao {

    private DBHelper dbHelper;

    public CacheDao(Context context) {
        this.dbHelper = DBHelper.getInstance(context);
    }

    public CacheDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void set(String key,String value){
        if (count(key) > 0){
            update(key, value);
        }else {
            add(key, value);
        }
    }

    public String get(String key){
        String v = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select _value from t_cache where _key=?", new String[] { key });
        if (cursor.moveToFirst()) {
            v= cursor.getString(cursor.getColumnIndex("_value"));
        }
        return v;
    }

    private int count(String key){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(_key) from t_cache where _key=?", new String[] { key });
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private void add(String key,String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();// 取得数据库操作
        db.execSQL("insert into t_cache (_key, _value, update_time) values(?, ?, ?)",
                new Object[] { key, value, TimeUtils.getCurrentTime()});
    }

    private void update(String key,String value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update t_cache set _value=?,update_time=? where _key=?", new Object[] { value, TimeUtils.getCurrentTime(), key });
    }

    public void remove(String key) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from t_cache where _key=?", new Object[] { key });
    }
}
