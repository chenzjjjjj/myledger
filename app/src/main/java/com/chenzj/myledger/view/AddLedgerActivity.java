package com.chenzj.myledger.view;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chenzj.myledger.R;
import com.chenzj.myledger.dao.LedgerDao;
import com.chenzj.myledger.model.Classification;
import com.chenzj.myledger.utils.KeyboardUtils;
import com.chenzj.myledger.view.ui.adapter.ClassifyAdapter;
import com.chenzj.myledger.view.ui.adapter.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class AddLedgerActivity extends AppCompatActivity {

    EditText ledger_amount;
    ClassifyAdapter mAdapter;
    int type = 0;
    private List<Classification> mDatas = new ArrayList<>();
    private LedgerDao ledgerDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ledger);
        ledgerDao = new LedgerDao(this);
        ledger_amount = findViewById(R.id.et_ledger_amount);
        setClassify();
        mAdapter = new ClassifyAdapter(this, mDatas);
        mAdapter.setClickCallback(new ClassifyAdapter.OnItemClickCallback<Classification>() {
            @Override
            public void onClick(View view, Classification info) {
                Toast.makeText(AddLedgerActivity.this,info.getClassify_name(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, Classification info) {

            }
        });

        //创建底部数字键盘
        final KeyboardUtils keyboardUtils = new KeyboardUtils(AddLedgerActivity.this);
        keyboardUtils.setOnOkClick(new KeyboardUtils.OnOkClick() {
            @Override
            public void onOkClick() {
                startActivity(new Intent(AddLedgerActivity.this, AddLedgerActivity.class));
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
                        mAdapter.notifyDataSetChanged();
                        break;
                    case  R.id.radio_income:
                        type = 1;
                        setClassify();
                        mAdapter.notifyDataSetChanged();
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

    private void setClassify(){
        mDatas.clear();
        mDatas = ledgerDao.findClassifyBytype(type);
//        for (int i=0; i<14; i++){
//            Classification classification = new Classification();
//            classification.setClassify_name(name+i);
//            mDatas.add(classification);
//        }
    }
}