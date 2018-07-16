package com.example.trt.servicedownload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trt.servicedownload.R;
import com.example.trt.servicedownload.module.Ffile;

import java.util.List;

import cn.lemon.view.adapter.BaseViewHolder;
import cn.lemon.view.adapter.RecyclerAdapter;

/**
 * Created by Trt on 2018/6/23.
 */

public class MyRecyclerAdapter extends RecyclerAdapter {
    private List<Ffile> mData;
    private LayoutInflater mInflater;
    private int mLayoutId;
    public MyRecyclerAdapter(Context context, List data,int layoutId) {
        super(context);
        this.mData=data;
        this.mInflater=LayoutInflater.from(mContext);
        this.mLayoutId=layoutId;

    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(mLayoutId,parent);
        ViewHolder holder=new ViewHolder(itemView);
        System.out.println("===1188");
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            convert((ViewHolder) holder,mData.get(position));
    }
    public void convert(ViewHolder holder, Ffile item) {
        holder.setText(R.id.file_name,item.getFilename());
    }

}
