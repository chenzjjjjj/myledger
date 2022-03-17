package com.chenzj.myledger.thread;

import android.content.Context;
import com.chenzj.myledger.common.Constant;
import com.chenzj.myledger.dao.CacheDao;
import com.chenzj.myledger.dao.LedgerDao;
import com.chenzj.myledger.model.Classification;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/14 17:41
 */
public class DataInitThread implements Runnable{
    private String[] costClassify = {"餐饮","购物","零食","水果","日用","买菜","学习","医疗","交通","娱乐","礼物","通讯","服饰","维修","健身","住房","红包"};
    private String[] incomeClassify = {"工资","奖金","收红包","生活费","投资","报销","退款","医疗"};
    private Context context;

    public DataInitThread(Context context){
        this.context = context;
    }

    @Override
    public void run() {
        CacheDao cacheDao = new CacheDao(context);
        cacheDao.set(Constant.IS_INIT, "Y");
        List<Classification> classList = new ArrayList<>();
        for (String costname : costClassify){
            Classification classification = new Classification();
            classification.setType(0);
            classification.setClassify_name(costname);
            classList.add(classification);
        }

        for (String name : incomeClassify){
            Classification classification = new Classification();
            classification.setType(1);
            classification.setClassify_name(name);
            classList.add(classification);
        }
        LedgerDao ledgerDao = new LedgerDao(context);
        ledgerDao.insertClassification(classList);
    }

}
