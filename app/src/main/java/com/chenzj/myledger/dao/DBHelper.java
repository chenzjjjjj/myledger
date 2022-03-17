package com.chenzj.myledger.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.chenzj.myledger.common.Constant;
import com.chenzj.myledger.utils.Logger;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/2/14 21:42
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "ledger.db"; //数据库名称
    private static final int DB_VERSION = 1; //数据库版本
    private static DBHelper helper;
    private static final String TAG = DBHelper.class.getSimpleName();

    public synchronized static DBHelper getInstance(Context context) {
        if (helper == null) {
            Logger.d(TAG, "======instance初始化"+context);
            helper = new DBHelper(context);
        }
        return helper;
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库sql语句 并 执行
        db.execSQL(Constant.T_USER_SQL);
        db.execSQL(Constant.T_CLASSIFICATION_SQL);
        db.execSQL(Constant.T_LEDGER_SQL);
        db.execSQL(Constant.LOCALSTORAGE_SQL);
//        db.execSQL("insert into t_cache (_key, _value, update_time) values('"+Constant.CURRENT_USER+"', '', '1980-02-20 00:00:00')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void closeHelper(){
        helper.close();
    }
}
