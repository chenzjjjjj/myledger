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
import com.chenzj.myledger.model.Ledger;
import com.chenzj.myledger.utils.TimeUtils;
import com.chenzj.myledger.view.AddLedgerActivity;
import com.chenzj.myledger.view.ui.adapter.DayItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;

public class HomeFragment extends Fragment {

    private ListView listView;
    private LedgerDao ledgerDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ledgerDao = new LedgerDao(getContext());
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        showListView(root);
        FloatingActionButton fab = root.findViewById(R.id.fab_add_ledger);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddLedgerActivity.class));
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                FragmentManager fm = getParentFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                AddLedgerFragment addLedgerFragment = new AddLedgerFragment();
//                ft.replace(R.id.nav_host_fragment, addLedgerFragment);
//                ft.commit();
            }
        });
        return root;
    }

    private void showListView(View view){
        listView = view.findViewById(R.id.listview_ledger);
        DayItemAdapter listAdapter = new DayItemAdapter(getContext(), R.layout.view_day_item, getDayLedgers());
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

    private List<DayLedger> getDayLedgers(){
        List<DayLedger> dayLedgers  = ledgerDao.findDayledgersBymonth(TimeUtils.date2stringMonth(new Date()));
//        for (int i=2; i<6; i++){
//            DayLedger dayLedger = new DayLedger();
//            dayLedger.setDate("3月"+i+"日 周"+i);
//            dayLedger.setIncomeTotal(i*1000);
//            dayLedger.setCostTotal(i*100);
//            dayLedger.setLedgers(getLedgers(i));
//            dayLedgers.add(dayLedger);
//        }
        return dayLedgers;
    }

    private List<Ledger> getLedgers(int num){
        if (num == 0){
            num = new Random().nextInt(10);
        }
        List<Ledger> ledgerVos = new ArrayList<>();
        for(int i=0; i<num; i++){
            Ledger ledgerVo = new Ledger();
            ledgerVo.setAmount(new Random().nextDouble()*100);
            int type = i%2;
            ledgerVo.setType(type);
            ledgerVo.setClassify( (type < 1 ? "支出":"收入") + i);
            ledgerVo.setIcon( type < 1 ? R.drawable.ic_cost_2 : R.drawable.ic_income_2);
            ledgerVos.add(ledgerVo);
        }
        return ledgerVos;
    }

}