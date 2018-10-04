package com.example.trt.servicedownload.adapter;

import android.content.Context;
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

public class DownAdapter extends BaseAdapter<DownloadFile> {

    private DownAdapter.OnItemClickListener onItemClickListener;

    public DownAdapter(Context context, List<DownloadFile> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public void setOnItemClickListener(DownAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    @Override
    public void convert(final ViewHolder holder, DownloadFile item) {
        holder.setText(R.id.file_name,item.getFilename());
        holder.setText(R.id.file_size, item.getMaxsize());
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

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

}
