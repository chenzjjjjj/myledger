package com.chenzj.myledger.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.chenzj.myledger.model.User;
import com.chenzj.myledger.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/2/19 17:12
 */
public class UserDao {
    private DBHelper dbHelper;

    public UserDao(Context context) {
        this.dbHelper = DBHelper.getInstance(context);
    }

    public UserDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void add(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();// 取得数据库操作
        db.execSQL("insert into t_user (account, username,password) values(?, ?, ?)",
                new Object[] { user.getAccount(), user.getUsername(), user.getPassword()});
    }

    public void delete(String account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from t_user where account=?", new Object[] { account });
    }

    public void update(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        StringBuffer sql = new StringBuffer("update t_user set ");
        List<String> args = new ArrayList<>();
        if (StringUtils.isNotBlank(user.getUsername())){
            sql.append("username=?");
            args.add(user.getUsername());
        }
        if (StringUtils.isNotBlank(user.getPassword())){
            if (sql.lastIndexOf("?") > 0)
                sql.append(", ");
            sql.append("password=?");
            args.add(user.getPassword());
        }
        sql.append(" where account=?");
        args.add(user.getAccount());
        db.execSQL(sql.toString(), args.toArray());
    }

    public User findUserByAccount(String account) {
        User user = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select * from t_user where account=?", new String[] { account });
        if (cursor.moveToFirst()) {// 依次取出数据 account, username,password
            user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            user.setAccount(cursor.getString(cursor.getColumnIndex("account")));
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> lists = new ArrayList<User>();
        User user = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Cursor cursor=db.rawQuery("select * from t_users limit ?,?", new
        // String[]{offset.toString(),maxLength.toString()});
        // //这里支持类型MYSQL的limit分页操作

        Cursor cursor = db.rawQuery("select * from t_users ", null);
        while (cursor.moveToNext()) {
            user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            user.setAccount(cursor.getString(cursor.getColumnIndex("account")));
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            lists.add(user);
        }
        return lists;
    }

    public long getCount() {//统计所有记录数
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(user_id) from t_user ", null);
        cursor.moveToFirst();
        return cursor.getLong(0);
    }

}
