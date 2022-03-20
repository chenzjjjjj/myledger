package com.chenzj.myledger.view.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import com.chenzj.myledger.R;
import com.chenzj.myledger.dao.LedgerDao;
import com.chenzj.myledger.model.DayLedger;
import com.chenzj.myledger.model.Ledger;
import com.chenzj.myledger.model.MonthTotal;
import com.chenzj.myledger.view.AddLedgerActivity;
import com.chenzj.myledger.view.ui.home.HomeViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/6 15:06
 */
public class DayItemAdapter extends ArrayAdapter<DayLedger> {

    private Context context; /*运行环境*/
    private List<DayLedger> dayLedgers;  /*数据源*/
    private LayoutInflater listContainer; // 视图容器
    private int resource;
    Ledger chooseledger;
    private HomeViewModel homeViewModel;
    private MonthTotal monthTotal = null;

    public DayItemAdapter(@NonNull Context context, int resource, @NonNull List<DayLedger> objects) {
        super(context, resource, objects);
        this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.context=context;
        this.dayLedgers=objects;
        this.resource = resource;
    }

    class DayLedgerView {
        TextView textDate;
        TextView textIncomeTotal;
        TextView textCostTotal;
        ListView dayListView;
    }

    public void setHomeViewModel(HomeViewModel homeViewModel){
        this.homeViewModel = homeViewModel;
        monthTotal = homeViewModel.getMonthTotalMLData().getValue();
    }

    public int getCount() {
        return dayLedgers.size();
    }

    public DayLedger getItem(int position) {
        return dayLedgers.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        DayLedgerView dayLedgerView;
        if (convertView == null) {
            itemView = listContainer.inflate(resource, parent, false);//加载布局
            dayLedgerView = new DayLedgerView();
            dayLedgerView.textDate = itemView.findViewById(R.id.text_day_date);
            dayLedgerView.textIncomeTotal = itemView.findViewById(R.id.text_income_total);
            dayLedgerView.textCostTotal = itemView.findViewById(R.id.text_cost_total);
            dayLedgerView.dayListView = itemView.findViewById(R.id.listview_day);
            itemView.setTag(dayLedgerView);
        }else {
            itemView = convertView;
            dayLedgerView = (DayLedgerView) itemView.getTag();
        }

        DayLedger dayLedger = getItem(position);
        dayLedgerView.textDate.setText(dayLedger.getDate());
        dayLedgerView.textIncomeTotal.setText("收入："+dayLedger.getIncomeTotal());
        dayLedgerView.textCostTotal.setText("支出："+dayLedger.getCostTotal());
        // 对子listview进行加载，属于嵌套了一个listview
        ListLedgerAdapter listAdapter = new ListLedgerAdapter(getContext(), R.layout.view_item, dayLedger.getLedgers());
        dayLedgerView.dayListView.setAdapter(listAdapter);
        setListViewHeightBasedOnChildren(dayLedgerView.dayListView);

        //子listView注册一个元素点击事件监听器
        dayLedgerView.dayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //当某个元素被点击时调用该方法
            public void onItemClick(AdapterView<?> parent, View view, int position,//parent就是ListView，view表示Item视图，position表示数据索引
                                    long id) {
//                TextView item = view.findViewById(R.id.text_item);
                //记录点击的列
                ListLedgerAdapter adapter = (ListLedgerAdapter) parent.getAdapter();
                chooseledger = adapter.getItem(position);
                showBottomDialog(adapter, position);
//                Toast.makeText(getContext(), item.getText(),Toast.LENGTH_LONG).show();
            }
        });
        return itemView;
    }

    /**
     * 底部弹窗
     * @param adapter
     * @param position
     */
    public void showBottomDialog(ListLedgerAdapter adapter, int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_ledger);
        //给布局设置透明背景色，让图片突出来
        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet)
                .setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));

        bottomSheetDialog.findViewById(R.id.tv_edit_ledger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳到编辑页面
                Intent intent = new Intent(getContext(), AddLedgerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("classifyId",chooseledger.getClassifyId());
                bundle.putInt("id",chooseledger.getId());
                bundle.putDouble("amount",chooseledger.getAmount());
                bundle.putInt("type",chooseledger.getType());
                bundle.putString("remark",chooseledger.getRemark());
                bundle.putString("date",chooseledger.getInsertTime());
                intent.putExtras(bundle);
                getContext().startActivity(intent);
                bottomSheetDialog.cancel();
            }
        });

        bottomSheetDialog.findViewById(R.id.tv_remove_ledger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除当前记录
                Ledger ledger = adapter.getItem(position);
                LedgerDao ledgerDao = new LedgerDao(getContext());
                ledgerDao.delete(ledger.getId());
                removeLedger(ledger);
                notifyDataSetChanged(); // 刷新列表
                bottomSheetDialog.cancel();
            }
        });

        bottomSheetDialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }

    public void refresh(List<DayLedger> newList) {
        dayLedgers.clear();
        dayLedgers.addAll(newList);
        notifyDataSetChanged();
    }

    public void add(DayLedger dayLedger) {
        if (dayLedgers == null) {
            dayLedgers = new ArrayList<>();
        }
        dayLedgers.add(dayLedger);
        notifyDataSetChanged();
    }

    public void remove(DayLedger dayLedger) {
        if(dayLedgers != null) {
            dayLedgers.remove(dayLedger);
        }
        notifyDataSetChanged();
    }

    /**
     * 删除子list里面的元素
     * @param ledger
     */
    public void removeLedger(Ledger ledger){
        String date = ledger.getInsertTime();
        for (int i=0; i<dayLedgers.size(); i++){
            DayLedger dayLedger = dayLedgers.get(i);
            if (dayLedger.getDate().equals(date)){
                List<Ledger> ledgerList = dayLedger.getLedgers();
                int size = ledgerList.size();
                for (int j=0; j<size; j++){
                    if (ledger.getId() == ledgerList.get(j).getId()){
                        ledgerList.remove(j);
                        if (ledger.getType() == 0) {
                            dayLedger.setCostTotal(dayLedger.getCostTotal() - ledger.getAmount());
                            monthTotal.setCostTotal(monthTotal.getCostTotal() - ledger.getAmount());
                        }else {
                            dayLedger.setIncomeTotal(dayLedger.getIncomeTotal() - ledger.getAmount());
                            monthTotal.setIncomeTotal(monthTotal.getIncomeTotal() - ledger.getAmount());
                        }
                        //将数据放入到viewmodel里面，刷新数据
                        homeViewModel.getMonthTotalMLData().setValue(monthTotal);
                        break;
                    }
                }
                break;
            }
        }
    }

    public void remove(int position) {
        if(dayLedgers != null) {
            dayLedgers.remove(position);
        }
        notifyDataSetChanged();
    }

    /**
     * 由于是嵌套的子listview，需要根据item计算高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListLedgerAdapter listAdapter = (ListLedgerAdapter) listView.getAdapter();
        ArrayAdapter listAdapter = (ArrayAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
