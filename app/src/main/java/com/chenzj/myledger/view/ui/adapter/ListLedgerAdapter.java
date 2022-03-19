package com.chenzj.myledger.view.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.chenzj.myledger.R;
import com.chenzj.myledger.model.DayLedger;
import com.chenzj.myledger.model.Ledger;

import java.util.List;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/6 15:06
 */
public class ListLedgerAdapter extends ArrayAdapter<Ledger> {

    private Context context; /*运行环境*/
    private List<Ledger> ledgers;  /*数据源*/
    private LayoutInflater listContainer; // 视图容器
    private int resource;

    public ListLedgerAdapter(@NonNull Context context, int resource, @NonNull List<Ledger> objects) {
        super(context, resource, objects);
        this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.context=context;
        this.ledgers=objects;
        this.resource = resource;
    }

    class LedgerItemView {
        ImageView ivIcon;
        TextView tvItemName;
        TextView tvAmount;
        LinearLayout layoutDayLine;
    }


    public int getCount() {
        return ledgers.size();
    }

    public Ledger getItem(int position) {
        return ledgers.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        LedgerItemView ledgerItemView;
        if (convertView == null) {
            itemView = listContainer.inflate(resource, parent, false);//加载布局
            ledgerItemView = new LedgerItemView();
            ledgerItemView.ivIcon = itemView.findViewById(R.id.ledger_item_icon);
            ledgerItemView.tvItemName = itemView.findViewById(R.id.text_item);
            ledgerItemView.tvAmount = itemView.findViewById(R.id.text_amount);
//            ledgerItemView.layoutDayLine = itemView.findViewById(R.id.list_day_line);
            itemView.setTag(ledgerItemView);
        }else {
            itemView = convertView;
            ledgerItemView = (LedgerItemView) itemView.getTag();
        }

        /**
         * 这里根据数据为收入或者支出，动态设置不同的图标以及字体颜色
         */
//        if (position == 1 ){
//            View dayView = listContainer.inflate(R.layout.view_day_item, null);
//            ledgerItemView.layoutDayLine.addView(dayView);
//        }
        Ledger ledger = getItem(position);
        int type = ledger.getType();
        String amount = (type < 1 ? "-":"+") + ledger.getAmount();
        ledgerItemView.ivIcon.setImageResource(ledger.getIcon());
        ledgerItemView.tvItemName.setText(ledger.getClassify());
        ledgerItemView.tvAmount.setText(amount);
        //修改金额的颜色
        int color = type < 1 ? R.color.red_light: R.color.green_1;
        ledgerItemView.tvAmount.setTextColor(getContext().getResources().getColor(color));
//        textAmount.setTextColor(Color.parseColor("#4D7CFE"));  //或者使用这种方式设置颜色
        return itemView;
    }


    public void remove(int poi) {
        if(ledgers != null) {
            ledgers.remove(poi);
        }
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
