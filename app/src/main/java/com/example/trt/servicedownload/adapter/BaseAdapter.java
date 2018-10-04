package com.example.trt.servicedownload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trt.servicedownload.BaseActivity;
import com.example.trt.servicedownload.module.Ffile;

import java.util.List;

/**
 * Created by Trt on 2018/6/23.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.ViewHolder>{
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mData;
    protected int mLayoutId;
    public BaseAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext=context;
        this.mInflater=LayoutInflater.from(mContext);
        this.mData=datas;
        this.mLayoutId=layoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(mLayoutId,parent,false);
        ViewHolder holder=new ViewHolder(itemView);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(holder,mData.get(position));
    }

    public void updateData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public abstract void convert(ViewHolder holder,T item);

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mConvertView;
        SparseArray<View> views;
        public ViewHolder(View itemView) {
            super(itemView);
            mConvertView=itemView;
            views=new SparseArray<>();
        }
        public <T extends View> T getView(int viewId){
            View view=views.get(viewId);
            if (view==null){
                view=mConvertView.findViewById(viewId);
                views.put(viewId,view);
            }
            return (T) view;
        }
        public BaseAdapter.ViewHolder setText(int viewId, String str){
            TextView tv=getView(viewId);
            tv.setText(str);
            return this;
        }
    }
}
