package com.chenzj.myledger.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chenzj.myledger.R;
import com.chenzj.myledger.dao.LedgerDao;
import com.chenzj.myledger.model.Classification;
import com.chenzj.myledger.model.Ledger;
import com.chenzj.myledger.model.User;
import com.chenzj.myledger.utils.KeyboardUtils;
import com.chenzj.myledger.utils.TimeUtils;
import com.chenzj.myledger.view.ui.adapter.ClassifyAdapter;
import com.chenzj.myledger.view.ui.adapter.DividerGridItemDecoration;
import com.chenzj.myledger.view.ui.myinfo.MyInfoViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddLedgerActivity extends AppCompatActivity implements DatePicker.OnDateChangedListener {

    EditText ledger_amount;
    TextView ledger_date;
    EditText et_ledger_remark;
    ClassifyAdapter mAdapter;
    Classification chooseClassify;
    private int type = 0;
    private List<Classification> mDatas = new ArrayList<>();
    private LedgerDao ledgerDao;
    private MyInfoViewModel myInfoViewModel;
    private User user;
    private StringBuffer date;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ledger);
        ledgerDao = new LedgerDao(this);
        ledger_amount = findViewById(R.id.et_ledger_amount);
        ledger_date = findViewById(R.id.et_ledger_date);
        et_ledger_remark = findViewById(R.id.et_ledger_remark);
        //从ViewModel中获取用户
        myInfoViewModel = new ViewModelProvider(this).get(MyInfoViewModel.class);
        user = myInfoViewModel.getUserMLData().getValue();

        //初始化日期
        initDateTime();
        date = new StringBuffer();
        ledger_date.setText(getDate());
        ledger_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        setClassify();
        mAdapter = new ClassifyAdapter(this, mDatas);
        mAdapter.setClickCallback(new ClassifyAdapter.OnItemClickCallback<Classification>() {
            @Override
            public void onClick(View view, Classification info) {
                chooseClassify = info;
                Snackbar.make(view, info.getClassify_name(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }

            @Override
            public void onLongClick(View view, Classification info) {

            }
        });

        //创建底部数字键盘
        final KeyboardUtils keyboardUtils = new KeyboardUtils(AddLedgerActivity.this);
        //给数字键盘上的确定按钮绑定时间
        keyboardUtils.setOnOkClick(new KeyboardUtils.OnOkClick() {
            @Override
            public void onOkClick() {
                AddLedger();
                AddLedgerActivity.this.finish();
            }
        });
        keyboardUtils.attachTo(ledger_amount);

        RadioGroup radioGroup = findViewById(R.id.radioGroup_classify);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case  R.id.radio_cost:
                        type = 0;
                        setClassify();
                        mAdapter.refresh(mDatas);
                        break;
                    case  R.id.radio_income:
                        type = 1;
                        setClassify();
                        mAdapter.refresh(mDatas);
                        break;
                }
            }
        });

        //创建列表
        RecyclerView recyclerView = findViewById(R.id.view_classify_item);
        //网格布局
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        // 设置滚动方向
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gridLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
    }

    private void AddLedger() {
        Ledger ledger = new Ledger();
        String amount = ledger_amount.getText().toString();
        String cdate = year+"-"+getMonth()+"-"+getDay();
        String remark = et_ledger_remark.getText().toString();
        ledger.setUserId(user.getUserId());
        ledger.setAmount(Double.parseDouble(amount));
        ledger.setType(type);
        ledger.setRemark(remark);
        ledger.setInsertTime(cdate);
        ledger.setClassifyId(chooseClassify.getClassify_id());
        ledgerDao.add(ledger);
    }

    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void showDateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ledger_date.setText(getDate());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_date, null);
        final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        dialog.setTitle("选择日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month - 1, day, this);
        datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear + 1; //月份下标是从0开始的，值为0时表示一月份
        this.day = dayOfMonth;
    }

    private void setClassify(){
        mDatas = ledgerDao.findClassifyBytype(type);
        chooseClassify = mDatas.get(0);
    }

    private String getDate(){
        if (date.length() > 0) { //清除上次记录的日期
            date.delete(0, date.length());
        }
        date.append(year).append(TimeUtils.YEAR_SPACER).append(getMonth()).append(TimeUtils.MONTH_SPACER).append(getDay()).append(TimeUtils.DAY_SPACER);
        return date.toString();
    }

    private String getMonth(){
        return (month>9?"":"0") + month;
    }

    private String getDay(){
        return (day>9?"":"0") + day;
    }
}