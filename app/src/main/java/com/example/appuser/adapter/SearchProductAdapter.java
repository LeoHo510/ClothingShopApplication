package com.example.appuser.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appuser.Interface.ItemClickListener;
import com.example.appuser.R;
import com.example.appuser.activity.ProductInfoActivity;
import com.example.appuser.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.MyViewHolder> {
    Context context;
    List<Product> list;

    public SearchProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.search_product_adapter, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SearchProductAdapter.MyViewHolder holder, int position) {
        Product product = list.get(position);
        if (product != null) {
            Glide.with(context).load(product.getUrl_img()).into(holder.imageView);
            holder.name.setText(product.getName());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            holder.price.setText("đ" + decimalFormat.format(Double.parseDouble(product.getPrice())));
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent info = new Intent(context, ProductInfoActivity.class);
                    info.putExtra("product", product);
                    info.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(info);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView name, price;
        ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.searchImage);
            name = itemView.findViewById(R.id.searchName);
            price = itemView.findViewById(R.id.searchPrice);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }
}
