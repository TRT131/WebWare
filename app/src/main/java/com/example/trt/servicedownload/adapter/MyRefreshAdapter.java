package com.example.trt.servicedownload.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trt.servicedownload.R;
import com.example.trt.servicedownload.module.Ffile;

import java.util.List;

/**
 * Created by Trt on 2018/6/23.
 */

public class MyRefreshAdapter extends BaseAdapter<Ffile> {
    public MyRefreshAdapter(Context context, List<Ffile> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Ffile item) {
        holder.setText(R.id.file_name,item.getFilename());
    }

}
