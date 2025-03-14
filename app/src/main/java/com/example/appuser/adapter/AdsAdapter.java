package com.example.appuser.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appuser.R;
import com.example.appuser.model.Ads;

import java.util.List;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.MyViewHolder> {
    Context context;
    List<Ads> list;
    ContentAdapter adapter;

    public AdsAdapter(Context context, List<Ads> list) {
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
            Ads ads = list.get(position);
            if (ads != null) {
                Glide.with(context).load(ads.getUrl()).into(holder.imageView);
                holder.title.setText(ads.getTitle());
                LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                holder.content.setLayoutManager(manager);
                holder.content.setHasFixedSize(true);
                adapter = new ContentAdapter(ads.getContent(), context);
                holder.content.setAdapter(adapter);
                holder.btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                holder.btnContactUs.setOnClickListener(new View.OnClickListener() {
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
        AppCompatButton btnDetails, btnContactUs;
        TextView title;
        RecyclerView content;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnContactUs = itemView.findViewById(R.id.btnContactUs);
            title = itemView.findViewById(R.id.titleAds);
            content = itemView.findViewById(R.id.recycleContent);
        }
    }
}
