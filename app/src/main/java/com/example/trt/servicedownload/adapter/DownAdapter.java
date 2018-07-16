package com.example.trt.servicedownload.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trt.servicedownload.R;
import com.example.trt.servicedownload.module.Ffile;
import com.example.trt.servicedownload.module.entity.DownloadFile;
import com.example.trt.servicedownload.util.FormetFileSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trt on 2018/5/23.
 */

public class DownAdapter extends RecyclerView.Adapter<DownAdapter.ViewHolder> {
    private List<DownloadFile> mData;

    private DownAdapter.OnItemClickListener onItemClickListener;

    public DownAdapter(List<DownloadFile> data) {
        this.mData = data;
    }
    public void updateData(List<DownloadFile> data) {
        this.mData = data;
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(DownAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        ViewHolder holder= new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setText(R.id.file_name,mData.get(position).getFilename());
        holder.setText(R.id.file_size, mData.get(position).getMaxsize());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView,pos);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData==null?0:mData.size();
    }

//    public void addNewItem() {
//        if(mData == null) {
//            mData = new ArrayList<>();
//        }
//        System.out.println("1188");
//        mData.add(0, new Ffile(0,"新文件"));
//        notifyItemInserted(0);
//    }

    public void deleteItem() {
        if(mData == null || mData.isEmpty()) {
            return;
        }
        mData.remove(0);
        notifyItemRemoved(0);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

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
        public ViewHolder setText(int viewId,String str){
            TextView tv=getView(viewId);
            tv.setText(str);
            return this;
        }

    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

}
