package com.example.appuser.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.ArrayList;
import java.util.List;

public class FavorAdapter extends RecyclerView.Adapter<FavorAdapter.MyViewHolder> {
    Context context;
    List<Product> list;
    private boolean isEditMode = false;
    private List<Product> CheckedProducts = new ArrayList<>();

    public List<Product> getCheckedProducts() {
        return CheckedProducts;
    }

    public void setList(List<Product> list) {
        this.list = list;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        notifyDataSetChanged();
    }

    public FavorAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FavorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.favor_product_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavorAdapter.MyViewHolder holder, int position) {
        Product product = list.get(position);
        if (product != null) {
            Glide.with(context).load(product.getUrl_img()).into(holder.imageView);
            holder.txtName.setText(product.getName());
            DecimalFormat decimalFormat = new DecimalFormat("đ###,###,###");
            holder.txtPrice.setText(decimalFormat.format(Double.parseDouble(product.getPrice())));
            holder.checkBox.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        CheckedProducts.add(product);
                    } else {
                        CheckedProducts.remove(product);
                    }
                }
            });
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent favor = new Intent(context, ProductInfoActivity.class);
                    favor.putExtra("product", product);
                    view.getContext().startActivity(favor);
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
        TextView txtName, txtPrice;
        CheckBox checkBox;
        ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.favorImage);
            txtName = itemView.findViewById(R.id.favorName);
            txtPrice = itemView.findViewById(R.id.favorPrice);
            checkBox = itemView.findViewById(R.id.favorCheckBox);
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
