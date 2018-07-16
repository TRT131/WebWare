package com.example.trt.servicedownload.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lemon.view.adapter.BaseViewHolder;

/**
 * Created by Trt on 2018/6/23.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    public ViewHolder(View itemView) {
        super(itemView);
        mViews=new SparseArray<>();
    }
    public <T extends View> T getView(int viewId){
        View view=mViews.get(viewId);
        if (view==null){
            view=itemView.findViewById(viewId);
            mViews.append(viewId,view);
        }
        return (T) view;
    }
    public ViewHolder setText(int viewId,CharSequence text){
        TextView tv=getView(viewId);
        tv.setText(text);
        return this;
    }
    public ViewHolder setImageResourse(int viewId,int resourseId){
        ImageView iv=getView(viewId);
        iv.setImageResource(resourseId);
        return this;
    }
    public void setOnIntemClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }

    /**
     * 设置条目长按事件
     */
    public void setOnIntemLongClickListener(View.OnLongClickListener listener) {
        itemView.setOnLongClickListener(listener);
    }

}
