package com.chenzj.myledger.view.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.chenzj.myledger.R;
import com.chenzj.myledger.common.Constant;
import com.chenzj.myledger.dao.CacheDao;
import com.chenzj.myledger.dao.LedgerDao;
import com.chenzj.myledger.model.DayLedger;
import com.chenzj.myledger.model.MonthTotal;
import com.chenzj.myledger.model.User;
import com.chenzj.myledger.utils.StringUtils;
import com.chenzj.myledger.utils.TimeUtils;
import com.chenzj.myledger.view.AddLedgerActivity;
import com.chenzj.myledger.view.LoginActivity;
import com.chenzj.myledger.view.ui.adapter.DayItemAdapter;
import com.chenzj.myledger.view.ui.myinfo.MyInfoViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;

public class HomeFragment extends Fragment implements DatePicker.OnDateChangedListener{

    private ListView listView;
    private LedgerDao ledgerDao;
    private List<DayLedger> dayLedgers;
    private DayItemAdapter listAdapter;
    private boolean isRefresh = true;
    TextView tvMonth;
    TextView tvIncomeMonth;
    TextView tvCostMonth;
    private int year = 0, month = 0;
    private StringBuffer yyyymm = new StringBuffer();
    private String chooseMonth;
    private HomeViewModel homeViewModel;
    private MonthTotal monthTotal;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ledgerDao = new LedgerDao(getContext());
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        tvMonth = root.findViewById(R.id.et_ledger_month);
        tvIncomeMonth = root.findViewById(R.id.tv_income_month);
        tvCostMonth = root.findViewById(R.id.tv_cost_month);
        //从ViewModel中获取月收入支出数据
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        monthTotal = homeViewModel.getMonthTotalMLData().getValue();
        // 通过viewmodel实时监听总额的变化，并修改页面显示
        homeViewModel.getMonthTotalMLData().observe(getViewLifecycleOwner(), new Observer<MonthTotal>() {
            @Override
            public void onChanged(@Nullable MonthTotal mtotal) {
                tvIncomeMonth.setText("月收入："+mtotal.getIncomeTotal());
                tvCostMonth.setText("月支出："+mtotal.getCostTotal());
            }
        });

        // 初始化时间
        initDate();
        tvMonth.setText(chooseMonth);
        tvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        setDayLedgers();
        showListView(root);

        // 初始化悬浮按钮
        FloatingActionButton fab = root.findViewById(R.id.fab_add_ledger);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddLedgerActivity.class));
            }
        });
        return root;
    }

    public void initDate(){
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        chooseMonth = getYYYYmm(1);
    }

    private String getYYYYmm(int type){
        if (yyyymm.length() > 0) { //清除上次记录的日期
            yyyymm.delete(0, yyyymm.length());
        }
        if (type == 0){
            yyyymm.append(year).append("-").append(getMonth());
        }else {
            yyyymm.append(year).append(TimeUtils.YEAR_SPACER).append(getMonth()).append(TimeUtils.MONTH_SPACER);
        }
        return yyyymm.toString();
    }

    private String getMonth(){
        return (month>9?"":"0") + month;
    }

    @Override
    public void onResume() {
        // 每次进入页面刷新listview列表
        if (!isRefresh) {
            setDayLedgers();
            listAdapter.refresh(dayLedgers);
            isRefresh = true;
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        isRefresh = false;
    }

    private void showListView(View view){
        listView = view.findViewById(R.id.listview_ledger);
        listAdapter = new DayItemAdapter(getContext(), R.layout.view_day_item, dayLedgers);
        listAdapter.setHomeViewModel(homeViewModel);
        listView.setAdapter(listAdapter);
    }

    public void showDateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getYYYYmm(1);
                if ( !chooseMonth.equals(yyyymm.toString()) ) {
                    chooseMonth = yyyymm.toString();
                    tvMonth.setText(chooseMonth);
                    setDayLedgers();
                    listAdapter.refresh(dayLedgers);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(getContext(), R.layout.dialog_date_mo, null);
        final DatePicker datePicker = dialogView.findViewById(R.id.datePickermo);

        dialog.setTitle("选择日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month - 1, 1, this);
        datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        //只展示年月
        int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
        if (daySpinnerId != 0) {
            View daySpinner = datePicker.findViewById(daySpinnerId);
            if (daySpinner != null) {
                daySpinner.setVisibility(View.GONE);
            }
        }
    }

    public void setDayLedgers(){
        double incomeTotal = 0;
        double costTotal = 0;
        dayLedgers  = ledgerDao.findDayledgersBymonth2(getYYYYmm(0));
        for (DayLedger dayLedger : dayLedgers){
            costTotal += dayLedger.getCostTotal();
            incomeTotal += dayLedger.getIncomeTotal();
        }
        monthTotal.setCostTotal(costTotal);
        monthTotal.setIncomeTotal(incomeTotal);
        //将数据放入到viewmodel里面，刷新数据
        homeViewModel.getMonthTotalMLData().setValue(monthTotal);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear + 1; //月份下标是从0开始的，值为0时表示一月份
    }
}