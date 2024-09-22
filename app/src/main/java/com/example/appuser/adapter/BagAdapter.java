package com.example.appuser.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appuser.R;
import com.example.appuser.model.EventBus.CheckTotalEvent;
import com.example.appuser.model.Product;
import com.example.appuser.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class BagAdapter extends RecyclerView.Adapter<BagAdapter.MyViewHolder> {
    Context context;
    List<Product> list;

    public BagAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
        Paper.init(context);
    }

    public void setList(List<Product> list) {
        this.list = list;
    }
    private void sendUpdateBroadcast() {
        Intent intent = new Intent("update-bag-list");
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
        Paper.book().write("listbag", Utils.listBag);  // Đảm bảo lưu lại danh sách
    }
    private void removeItem(int position) {
        Utils.listBag.remove(list.get(position));
        list.remove(list.get(position));
        notifyDataSetChanged();
        sendUpdateBroadcast();
    }
    @NonNull
    @Override
    public BagAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.bag_product_adapter, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BagAdapter.MyViewHolder holder, int position) {
        Product product = list.get(position);
        if (product != null) {
            Glide.with(context).load(product.getUrl_img()).into(holder.image);
            holder.name.setText(product.getName());
            holder.info.setText(product.getInfo());
            holder.size.setText(String.valueOf(product.getSize()));
            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                   removeItem(position);
                   EventBus.getDefault().postSticky(new CheckTotalEvent());
                }
            });
            holder.btnQuantity.setText(String.valueOf(product.getQuantity()));
            holder.btnQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialog(holder.btnQuantity, holder.price, position);
                }
            });
            holder.price.setText("đ" + new DecimalFormat("###,###,###").format(Double.parseDouble(product.getPrice()) * product.getQuantity()));
        }
    }

    @SuppressLint("ResourceAsColor")
    private void showBottomSheetDialog(AppCompatButton btnQuantity, TextView txtPrice, int pos) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        String[] quantities = context.getResources().getStringArray(R.array.quantity_array);
        for (String quantity : quantities) {
            TextView item = new TextView(context);
            item.setText(quantity);
            item.setPadding(20, 20, 20, 20);
            item.setTextSize(18);
            item.setTextColor(R.color.black);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quantity.equals("Remove")) {
                        removeItem(pos);
                    } else {
                        btnQuantity.setText(quantity);
                        list.get(pos).setQuantity(Integer.parseInt(quantity));
                        double price = list.get(pos).getQuantity() * Double.parseDouble(list.get(pos).getPrice());
                        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                        txtPrice.setText("đ" + decimalFormat.format(price));
                        notifyDataSetChanged();
                        sendUpdateBroadcast();
                    }
                    bottomSheetDialog.dismiss();
                    EventBus.getDefault().postSticky(new CheckTotalEvent());
                }
            });
            ((ViewGroup) bottomSheetView).addView(item);
        }
        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image, btnRemove;
        TextView name, info, price, size;
        AppCompatButton btnQuantity;
        public MyViewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.imageBag);
            name = v.findViewById(R.id.nameBag);
            info = v.findViewById(R.id.infoBag);
            price = v.findViewById(R.id.priceBag);
            size = v.findViewById(R.id.sizeBag);
            btnRemove = v.findViewById(R.id.btnRemove);
            btnQuantity = v.findViewById(R.id.btnQuantity);
        }
    }
}
