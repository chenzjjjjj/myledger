package com.chenzj.myledger.model;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/20 21:09
 */
public class MonthTotal {
    private double costTotal = 0,
            incomeTotal = 0;

    public double getCostTotal() {
        return costTotal;
    }

    public void setCostTotal(double costTotal) {
        this.costTotal = costTotal;
    }

    public double getIncomeTotal() {
        return incomeTotal;
    }

    public void setIncomeTotal(double incomeTotal) {
        this.incomeTotal = incomeTotal;
    }
}
