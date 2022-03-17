package com.chenzj.myledger.view;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
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
import java.util.List;

public class AddLedgerActivity extends AppCompatActivity {

    EditText ledger_amount;
    EditText ledger_date;
    EditText et_ledger_remark;
    ClassifyAdapter mAdapter;
    Classification chooseClassify;
    int type = 0;
    private List<Classification> mDatas = new ArrayList<>();
    private LedgerDao ledgerDao;
    private MyInfoViewModel myInfoViewModel;
    User user;

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

        ledger_date.setText(TimeUtils.getCurrentDate());
        setClassify();
        mAdapter = new ClassifyAdapter(this, mDatas);
        mAdapter.setClickCallback(new ClassifyAdapter.OnItemClickCallback<Classification>() {
            @Override
            public void onClick(View view, Classification info) {
                chooseClassify = info;
//                Toast.makeText(AddLedgerActivity.this,info.getClassify_name(), Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(AddLedgerActivity.this, LedgerActivity.class));
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
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
    }

    private void AddLedger() {
        Ledger ledger = new Ledger();
        String amount = ledger_amount.getText().toString();
        String date = ledger_date.getText().toString();
        String remark = et_ledger_remark.getText().toString();
        ledger.setUserId(user.getUserId());
        ledger.setAmount(Double.parseDouble(amount));
        ledger.setType(type);
        ledger.setRemark(remark);
        ledger.setInsertTime(date);
        ledger.setClassifyId(chooseClassify.getClassify_id());
        ledgerDao.add(ledger);
    }

    private void setClassify(){
        mDatas = ledgerDao.findClassifyBytype(type);
    }
}