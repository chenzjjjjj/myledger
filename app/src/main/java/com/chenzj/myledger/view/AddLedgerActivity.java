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
    ImageView iv_back;
    EditText et_ledger_remark;
    ClassifyAdapter mAdapter;
    Classification chooseClassify;
    private int type = 0;
    private List<Classification> mDatas = new ArrayList<>();
    private LedgerDao ledgerDao;
    private MyInfoViewModel myInfoViewModel;
    private User user;
    private StringBuffer choosedate = new StringBuffer();;
    private int year, month, day;
    private int updateLedgerId = -1; //默认false，表示添加新账目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ledger);
        ledgerDao = new LedgerDao(this);
        ledger_amount = findViewById(R.id.et_ledger_amount);
        ledger_date = findViewById(R.id.et_ledger_date);
        et_ledger_remark = findViewById(R.id.et_ledger_remark);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddLedgerActivity.this.finish();
            }
        });
        //从ViewModel中获取用户
        myInfoViewModel = new ViewModelProvider(this).get(MyInfoViewModel.class);
        user = myInfoViewModel.getUserMLData().getValue();

        //如果过来的时候携带了数据则表示编辑账目了
        Bundle bundle = this.getIntent().getExtras();
        if (bundle!=null && !bundle.isEmpty()){
            //如果过来的时候携带了数据则表示编辑账目了
            updateLedgerId = bundle.getInt("id");
            chooseClassify = new Classification();
            chooseClassify.setClassify_id(bundle.getInt("classifyId"));
            ledger_amount.setText(""+bundle.getDouble("amount"));
            type = bundle.getInt("type");
            setClassify(); //初始化标签
            et_ledger_remark.setText(bundle.getString("remark"));
            String[] data = bundle.getString("date").split("-");
            year = Integer.parseInt(data[0]);
            month = Integer.parseInt(data[1]);
            day = Integer.parseInt(data[2]);
            ledger_date.setText(getDate());
            int posi = getPosition(chooseClassify);
            mAdapter = new ClassifyAdapter(this, mDatas, posi);
        }else { //否则就是添加账目
            updateLedgerId = -1;
            //初始化日期
            initDateTime();
            ledger_date.setText(getDate());
            setClassify(); //初始化标签
            chooseClassify = mDatas.get(0); //默认第一个
            mAdapter = new ClassifyAdapter(this, mDatas);
        }

        ledger_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        mAdapter.setClickCallback(new ClassifyAdapter.OnItemClickCallback<Classification>() {
            @Override
            public void onClick(View view, Classification info) {
                chooseClassify = info;
                Snackbar.make(view, info.getClassify_name(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
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
        radioGroup.check(type == 0 ?R.id.radio_cost : R.id.radio_income);
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

    private int getPosition(Classification chooseClassify) {
        int size = mDatas.size();
        for (int i=0; i<size; i++){
            if (mDatas.get(i).getClassify_id() == chooseClassify.getClassify_id()){
                return i;
            }
        }
        return 0;
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
        if ( updateLedgerId < 0 ){
            ledgerDao.add(ledger);
        }else {
            ledger.setId(updateLedgerId);
            ledgerDao.update(ledger);
        }
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
    }

    private String getDate(){
        if (choosedate.length() > 0) { //清除上次记录的日期
            choosedate.delete(0, choosedate.length());
        }
        choosedate.append(year).append(TimeUtils.YEAR_SPACER).append(getMonth()).append(TimeUtils.MONTH_SPACER).append(getDay()).append(TimeUtils.DAY_SPACER);
        return choosedate.toString();
    }

    private String getMonth(){
        return (month>9?"":"0") + month;
    }

    private String getDay(){
        return (day>9?"":"0") + day;
    }
}