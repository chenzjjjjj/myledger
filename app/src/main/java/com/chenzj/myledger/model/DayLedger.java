package com.chenzj.myledger.model;

import java.util.List;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/10 14:45
 */
public class DayLedger {
    private String date;
    private double costTotal = 0;
    private double incomeTotal = 0;
    private List<Ledger> ledgers;

    public DayLedger(){

    }
    public DayLedger(List<Ledger> ledgers){
        this.ledgers = ledgers;
        int cost = 0;
        int income = 0;
        for (Ledger ledger : ledgers){
            if (ledger.getType() == 0){
                this.costTotal += ledger.getAmount();
            }else {
                this.incomeTotal += ledger.getAmount();
            }
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

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

    public List<Ledger> getLedgers() {
        return ledgers;
    }

    public void setLedgers(List<Ledger> ledgers) {
        this.ledgers = ledgers;
    }

    public void reduceCostTotal(double cost){
        this.costTotal = this.costTotal - cost;
    }
}
