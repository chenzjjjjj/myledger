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
import androidx.recyclerview.widget.RecyclerView;
import com.chenzj.myledger.R;
import com.chenzj.myledger.common.Constant;
import com.chenzj.myledger.dao.CacheDao;
import com.chenzj.myledger.dao.LedgerDao;
import com.chenzj.myledger.model.DayLedger;
import com.chenzj.myledger.model.MonthTotal;
import com.chenzj.myledger.model.User;
import com.chenzj.myledger.utils.ArithUtils;
import com.chenzj.myledger.utils.StringUtils;
import com.chenzj.myledger.utils.TimeUtils;
import com.chenzj.myledger.view.AddLedgerActivity;
import com.chenzj.myledger.view.LoginActivity;
import com.chenzj.myledger.view.ui.adapter.DayItemAdapter;
import com.chenzj.myledger.view.ui.myinfo.MyInfoViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HomeFragment extends Fragment implements DatePicker.OnDateChangedListener{

    private ListView listView;
    private LedgerDao ledgerDao;
    private FloatingActionButton addFab;
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
        //???ViewModel??????????????????????????????
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        monthTotal = homeViewModel.getMonthTotalMLData().getValue();
        // ??????viewmodel???????????????????????????????????????????????????
        homeViewModel.getMonthTotalMLData().observe(getViewLifecycleOwner(), new Observer<MonthTotal>() {
            @Override
            public void onChanged(@Nullable MonthTotal mtotal) {
                tvIncomeMonth.setText("????????????"+ ArithUtils.get2plase(mtotal.getIncomeTotal()));
                tvCostMonth.setText("????????????"+ ArithUtils.get2plase(mtotal.getCostTotal()));
            }
        });

        // ???????????????
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

        // ?????????????????????
        addFab = root.findViewById(R.id.fab_add_ledger);
        addFab.setOnClickListener(new View.OnClickListener() {
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
        if (yyyymm.length() > 0) { //???????????????????????????
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
        // ????????????????????????listview??????
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
        View footerView = View.inflate(getContext(), R.layout.view_bottom_empty, null);
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_bottom_empty, null);
        listView.addFooterView(footerView); //????????????????????????
        //??????????????????
        listView.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){
                // ???????????????
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // ???????????????????????????
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        // ??????????????????
                        addFab.hide();
                    }else {
                        //??????
                        addFab.show();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    public void showDateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(getContext(), R.layout.dialog_date_mo, null);
        final DatePicker datePicker = dialogView.findViewById(R.id.datePickermo);

        dialog.setTitle("????????????");
        dialog.setView(dialogView);
        dialog.show();
        //???????????????????????????
        datePicker.init(year, month - 1, 1, this);
        datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        //???????????????
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
        //??????????????????viewmodel?????????????????????
        homeViewModel.getMonthTotalMLData().setValue(monthTotal);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear + 1; //??????????????????0??????????????????0??????????????????
    }
}