package com.example.appuser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appuser.R;
import com.example.appuser.model.Sales;

import java.util.List;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.MyViewHolder> {
    Context context;
    List<Sales> list;

    public AdsAdapter(Context context, List<Sales> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.ads_adapter_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdsAdapter.MyViewHolder holder, int position) {
        if (!list.isEmpty()) {
            Sales ads = list.get(position);
            if (ads != null) {
                Glide.with(context).load(ads.getUrl()).into(holder.imageView);
                holder.btnWatchNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        AppCompatButton btnWatchNow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            btnWatchNow = itemView.findViewById(R.id.btnWatchNow);
        }
    }
}
