package com.chenzj.myledger.common;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/2/19 14:19
 */
public class Constant {
    public final static String CURRENT_USER = "ledger:current_user";
    public final static String IS_INIT = "ledger:is_init";

    /**
     * 建表sql
     */
    public final static String T_USER_SQL = "CREATE TABLE t_user(user_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,account VARCHAR(30) not NULL,username VARCHAR(40) not NULL,password VARCHAR(20) not NULL);";
    public final static String T_CLASSIFICATION_SQL = "CREATE TABLE t_classification(classify_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,classify_name VARCHAR(255) not NULL,is_use TINYINT NULL DEFAULT 0,type TINYINT NULL)";
    public final static String T_LEDGER_SQL = "CREATE TABLE t_ledger(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,amount NUMERIC NULL,insert_time VARCHAR(19) not NULL,classify_id INTEGER not NULL,user_id INTEGER not NULL,type TINYINT not NULL,remark VARCHAR(200),CONSTRAINT FK_t_income_t_classification FOREIGN KEY (classify_id) REFERENCES t_classification (classify_id) ON DELETE No Action ON UPDATE No Action,CONSTRAINT FK_t_income_t_user FOREIGN KEY (user_id) REFERENCES t_user (user_id) ON DELETE No Action ON UPDATE No Action);";
    public final static String LOCALSTORAGE_SQL = "CREATE TABLE t_cache(_key VARCHAR(200) not NULL PRIMARY KEY,_value text not NULL,update_time VARCHAR(20) not NULL)";
}
