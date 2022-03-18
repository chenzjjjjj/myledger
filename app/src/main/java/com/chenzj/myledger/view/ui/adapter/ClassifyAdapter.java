package com.chenzj.myledger.view.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chenzj.myledger.R;
import com.chenzj.myledger.model.Classification;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @description: TODO
 * @author: chenzj
 * @date: 2022/3/12 16:24
 */
public class ClassifyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    List<Classification> classificationList;
    // 申明一个点击事件接口变量
    private OnItemClickCallback callback = null;
    private int selectedPosition = 0;

    public ClassifyAdapter(Context mContext, List<Classification> classificationList) {
        this.mContext = mContext;
        this.classificationList = classificationList;
    }

    public ClassifyAdapter(Context mContext, List<Classification> classificationList, OnItemClickCallback callback) {
        this.mContext = mContext;
        this.classificationList = classificationList;
        this.callback = callback;
    }

    public void setClickCallback(OnItemClickCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.view_classify_item, parent, false);
        return new ClassifyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ClassifyHolder classifyHolder = (ClassifyHolder) holder;
        classifyHolder.tv_classify_name.setText(classificationList.get(position).getClassify_name());
        holder.itemView.setSelected(selectedPosition == position);

        if(selectedPosition == position){
            classifyHolder.ic_classify.setImageAlpha(255);
        }else{
            classifyHolder.ic_classify.setImageAlpha(100);
        }
        classifyHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onClick(view,classificationList.get(position));
                notifyItemChanged(selectedPosition);
                selectedPosition = position; //选择的position赋值给参数，
                notifyItemChanged(selectedPosition);//刷新当前点击item
            }
        });
    }

    @Override
    public int getItemCount() {
        return classificationList.size();
    }


    public void add(List<Classification> newList) {
        int position = newList.size();
        classificationList.addAll(position, newList);
        notifyItemInserted(position);
    }

    public void refresh(List<Classification> newList) {
        classificationList.clear();
        classificationList.addAll(newList);
        notifyDataSetChanged();
    }

    public class ClassifyHolder extends RecyclerView.ViewHolder{
        public GridLayout mGridLayout;
        public TextView tv_classify_name;
        public ImageView ic_classify;

        public ClassifyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tv_classify_name = itemView.findViewById(R.id.text_classify_name);
            ic_classify = itemView.findViewById(R.id.icon_classify);
            mGridLayout = itemView.findViewById(R.id.gridlayout_classify);
//        tv_classify_name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext,mTV.getText(),Toast.LENGTH_SHORT).show();
//            }
//        });
        }

        public void setBackgroundColor(int color){
            mGridLayout.setBackgroundColor(color);
        }
    }

    public interface OnItemClickCallback<T> {
        // 点击事件
        void onClick(View view , T info);
        // 长按事件
        void onLongClick(View view , T info);
    }
}
