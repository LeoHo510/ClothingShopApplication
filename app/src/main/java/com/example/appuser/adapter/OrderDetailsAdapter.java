package com.example.appuser.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appuser.R;
import com.example.appuser.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.MyViewHolder> {
    Context context;
    List<Product> list;

    public OrderDetailsAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.orderdetails_adapter_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderDetailsAdapter.MyViewHolder holder, int position) {
        if (!list.isEmpty()) {
            Product product = list.get(position);
            Glide.with(context).load(product.getUrl_img()).into(holder.image);
            holder.name.setText(product.getName());
            holder.size.setText("Size: " + product.getSize());
            holder.quantity.setText("Quantity: " + product.getQuantity());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            holder.price.setText("Price: " + "đ" + decimalFormat.format(Double.parseDouble(product.getPrice())));
        }
    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, size, quantity, price;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageDetails);
            name = itemView.findViewById(R.id.nameDetails);
            size = itemView.findViewById(R.id.sizeDetails);
            quantity = itemView.findViewById(R.id.quantityDetails);
            price = itemView.findViewById(R.id.priceDetails);
        }
    }
}
