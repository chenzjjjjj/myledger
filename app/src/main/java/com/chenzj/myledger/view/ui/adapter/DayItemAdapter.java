package com.chenzj.myledger.view.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import com.chenzj.myledger.R;
import com.chenzj.myledger.model.DayLedger;

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
                TextView item = view.findViewById(R.id.text_item);
                Toast.makeText(getContext(), item.getText(),Toast.LENGTH_LONG).show();
            }
        });
        return itemView;
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
