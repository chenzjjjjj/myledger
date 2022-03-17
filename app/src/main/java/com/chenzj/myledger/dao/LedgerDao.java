package com.chenzj.myledger.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.chenzj.myledger.model.Classification;
import com.chenzj.myledger.model.DayLedger;
import com.chenzj.myledger.model.Ledger;
import com.chenzj.myledger.model.User;
import com.chenzj.myledger.utils.StringUtils;
import com.chenzj.myledger.utils.TimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/13 12:14
 */
public class LedgerDao {
    private DBHelper dbHelper;

    public LedgerDao(Context context) {
        this.dbHelper = DBHelper.getInstance(context);
    }

    public LedgerDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void add(Ledger ledger) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();// 取得数据库操作
        db.execSQL("insert into t_ledger (amount, insert_time,type, remark,classify_id,user_id) values(?,?,?,?,?,?)",
                new Object[] { ledger.getAmount(), ledger.getInsertTime(), ledger.getType(), ledger.getClassifyId(),ledger.getUserId()});
    }

    public void delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from t_ledger where id=?", new Object[] { id });
    }

    public void update(Ledger ledger) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        StringBuffer sql = new StringBuffer("update t_ledger set ");
        List<String> sqlarr = new ArrayList<>();
        List<String> args = new ArrayList<>();
        if (ledger.getAmount() != null && ledger.getAmount() > 0){
            sqlarr.add("amount=?");
            args.add(String.valueOf(ledger.getAmount()));
        }
        if (ledger.getClassifyId() > 0){
            sqlarr.add("classify_id=?");
            args.add(ledger.getClassifyId()+"");
        }
        if (StringUtils.isNotBlank(ledger.getRemark())){
            sqlarr.add("remark=?");
            args.add(ledger.getRemark());
        }
        sql.append(StringUtils.join(sqlarr.toArray(), ", "));
        sql.append(" where id=?");
        args.add(ledger.getId()+"");
        db.execSQL(sql.toString(), args.toArray());
    }

    public List<Ledger> findLedgersBytime(String time){
        List<Ledger> lists = new ArrayList<>();
        Ledger ledger = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Cursor cursor=db.rawQuery("select * from t_users limit ?,?", new
        // String[]{offset.toString(),maxLength.toString()});
        // //这里支持类型MYSQL的limit分页操作

        Cursor cursor = db.rawQuery("select * from t_ledger where insert_time=?", new String[]{time});
        while (cursor.moveToNext()) {
            ledger = new Ledger();
            ledger.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
            ledger.setId(cursor.getInt(cursor.getColumnIndex("id")));
            ledger.setClassifyId(cursor.getInt(cursor.getColumnIndex("classify_id")));
            ledger.setInsertTime(cursor.getString(cursor.getColumnIndex("insert_time")));
            ledger.setType(cursor.getInt(cursor.getColumnIndex("type")));
            ledger.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            ledger.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
            ledger.setClassify(findClassifyName(ledger.getClassifyId()));
            lists.add(ledger);
        }
        return lists;
    }

    public List<DayLedger> findDayledgersBymonth(String yyyy_mm){
        List<DayLedger> dayLedgers = new ArrayList<>();
        String currentMonth = TimeUtils.date2stringMonth(new Date());
        int maxDay = 0;
        if (currentMonth.equals(yyyy_mm)){ // 当前月份
            maxDay = TimeUtils.getDayNumOfMonth(null);
        }else {
            Date monthdate = null;
            try {
                monthdate = TimeUtils.monthStr2date(yyyy_mm);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            maxDay = TimeUtils.getDayNumOfMonth(monthdate);
        }

        for (int i=1; i<maxDay; i++){
            String dd = i < 10? "0"+i : i+"";
            String day = yyyy_mm + dd;
            List<Ledger> ledgers= findLedgersBytime(day);
            if (ledgers==null || ledgers.isEmpty()){
                continue;
            }
            DayLedger dayLedger = new DayLedger(ledgers);
            dayLedger.setDate(day);
            dayLedgers.add(dayLedger);
        }
        return dayLedgers;
    }

    public String findClassifyName(int  classify_id){
        String v = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select classify_name from t_classification where classify_id=?", new String[] { classify_id+"" });
        if (cursor.moveToFirst()) {
            v= cursor.getString(cursor.getColumnIndex("classify_name"));
        }
        return v;
    }

    public List<Classification> findClassifyBytype(int type){
        List<Classification> clalist = new ArrayList<>();
        Classification classification = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from t_classification where type=? and is_use='0'", new String[]{type+""});
        while (cursor.moveToNext()) {
            classification = new Classification();
            classification.setClassify_id(cursor.getInt(cursor.getColumnIndex("classify_id")));
            classification.setClassify_name(cursor.getString(cursor.getColumnIndex("classify_name")));
            classification.setType(cursor.getInt(cursor.getColumnIndex("type")));
            classification.setIs_use(cursor.getInt(cursor.getColumnIndex("is_use")));
            clalist.add(classification);
        }
        return clalist;
    }

    public void insertClassification(List<Classification> classificationList){
        String sql = "insert into t_classification(classify_name,type) values(?,?)";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransaction();
        for (Classification classify : classificationList) {
            stat.bindString(1, classify.getClassify_name());
            stat.bindLong(2, classify.getType());
            stat.executeInsert();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
}
