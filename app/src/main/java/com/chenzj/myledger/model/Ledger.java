package com.chenzj.myledger.model;

import com.chenzj.myledger.R;
import com.chenzj.myledger.utils.TimeUtils;

import java.util.Date;

/**
 * @description: 账单记录
 * @author: chenzj
 * @date: 2022/2/19 17:04
 */
public class Ledger {
    private int id;
    private Double amount;
    private String insertTime; // 格式为YYYY-mm-dd
    private String remark;  //备注
    private int classifyId = -1;
    private int userId = -1;
    private int type = 0; //0:支出，1：收入
    private int icon = 0;
    private String classify;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        setIcon(type < 1 ? R.drawable.ic_cost_2:R.drawable.ic_income_2);
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }
}
