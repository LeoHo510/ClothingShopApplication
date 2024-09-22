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
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appuser.Interface.ItemClickListener;
import com.example.appuser.R;
import com.example.appuser.activity.ProductInfoActivity;
import com.example.appuser.model.Product;
import com.example.appuser.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

import io.paperdb.Paper;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    Context context;
    List<Product> list;

    public ProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
        Paper.init(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_child_adapter, parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = list.get(position);
        if (product != null) {
            Glide.with(context).load(product.getUrl_img()).into(holder.childImage);
            holder.childTitle.setText(product.getName());
            holder.childInfo.setText(product.getInfo());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            holder.childPrice.setText("đ" + decimalFormat.format(Double.parseDouble(product.getPrice())));

            final boolean[] isFavor = {isProductInFavorList(product)};

            updateFavorIcon(holder, isFavor[0]);

            holder.childFavor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFavor[0]) {
                        Utils.listFavor.remove(product);
                    } else {
                        if (!Utils.listFavor.contains(product)) {
                            Utils.listFavor.add(product);
                        }
                    }
                    Paper.book().write("listfavor", Utils.listFavor);
                    sendUpdateBroadcast();

                    // Update isFavor and icon after clicking
                    isFavor[0] = !isFavor[0];
                    updateFavorIcon(holder, isFavor[0]);
                }
            });

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent p = new Intent(context, ProductInfoActivity.class);
                    p.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    p.putExtra("product", product);
                    context.startActivity(p);
                }
            });

            if (product.getInventory_quantity() == 0) {
                holder.imageSoldOut.setVisibility(View.VISIBLE);
            } else {
                holder.imageSoldOut.setVisibility(View.GONE);
            }
        }
    }

    private boolean isProductInFavorList(Product product) {
        for (Product favorProduct : Utils.listFavor) {
            if (favorProduct.getId() == product.getId()) {
                return true;
            }
        }
        return false;
    }

    private void updateFavorIcon(MyViewHolder holder, boolean isFavor) {
        if (isFavor) {
            holder.childFavor.setColorFilter(ContextCompat.getColor(context, R.color.pink));
        } else {
            holder.childFavor.setColorFilter(ContextCompat.getColor(context, R.color.white));
        }
    }

    private void sendUpdateBroadcast() {
        Intent intent = new Intent("update-favor-list");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Paper.book().write("listfavor", Utils.listFavor);  // Đảm bảo lưu lại danh sách
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView childImage, childFavor, imageSoldOut;
        TextView childTitle, childInfo, childPrice;
        ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            childFavor = itemView.findViewById(R.id.childFavor);
            childImage = itemView.findViewById(R.id.childImage);
            childInfo = itemView.findViewById(R.id.childInfo);
            childTitle = itemView.findViewById(R.id.childTitle);
            childPrice = itemView.findViewById(R.id.childPrice);
            imageSoldOut = itemView.findViewById(R.id.imageSoldOut);
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