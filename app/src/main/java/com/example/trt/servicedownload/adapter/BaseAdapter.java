package com.example.trt.servicedownload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Trt on 2018/6/23.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<ViewHolder>{
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

    public abstract void convert(ViewHolder holder,T item);

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
