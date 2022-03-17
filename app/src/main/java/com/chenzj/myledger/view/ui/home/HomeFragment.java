package com.chenzj.myledger.view.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.chenzj.myledger.R;
import com.chenzj.myledger.dao.LedgerDao;
import com.chenzj.myledger.model.DayLedger;
import com.chenzj.myledger.utils.TimeUtils;
import com.chenzj.myledger.view.AddLedgerActivity;
import com.chenzj.myledger.view.ui.adapter.DayItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;

public class HomeFragment extends Fragment {

    private ListView listView;
    private LedgerDao ledgerDao;
    List<DayLedger> dayLedgers;
    DayItemAdapter listAdapter;
    boolean isRefresh = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ledgerDao = new LedgerDao(getContext());
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setDayLedgers();
        showListView(root);
        FloatingActionButton fab = root.findViewById(R.id.fab_add_ledger);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddLedgerActivity.class));
            }
        });
        return root;
    }

    @Override
    public void onResume() {
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
        listView.setAdapter(listAdapter);

        //listView注册一个元素点击事件监听器
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            //当某个元素被点击时调用该方法
//            public void onItemClick(AdapterView<?> parent, View view, int position,//parent就是ListView，view表示Item视图，position表示数据索引
//                                    long id) {
//                Toast.makeText(getContext(), "",Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void setDayLedgers(){
        dayLedgers  = ledgerDao.findDayledgersBymonth2(TimeUtils.date2stringMonth(new Date()));
    }

}