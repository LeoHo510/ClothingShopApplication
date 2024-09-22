package com.example.appuser.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appuser.R;
import com.example.appuser.model.Order;
import com.example.appuser.model.Product;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    Context context;
    List<Order> orderList;
    OrderDetailsAdapter adapter;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.order_adapter_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.MyViewHolder holder, int position) {
        if (!orderList.isEmpty()) {
            Order order = orderList.get(position);
            if (order != null) {
                holder.id.setText("Order-" + order.getId());
                holder.status.setText("Status: Preparing");
                holder.date.setText(order.getDate());
                DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                holder.total_price.setText("Total Price: đ" + decimalFormat.format(Double.parseDouble(order.getTotalprice())));
                holder.name.setText("Name: " + order.getFirstname() + " " + order.getLastname());
                holder.email.setText("Email: " + order.getEmail());
                holder.address.setText("Address: " + order.getAddress());
                holder.phone_number.setText("Phone Number: " + order.getPhonenumber());
                LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                holder.recyclerView.setLayoutManager(manager);
                holder.recyclerView.setHasFixedSize(true);
                adapter = new OrderDetailsAdapter(context, order.getList());
                holder.recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList.isEmpty() ? 0 : orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView id, status, date, total_price, name, email, address, phone_number;
        RecyclerView recyclerView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.idOrder);
            status = itemView.findViewById(R.id.statusOrder);
            date = itemView.findViewById(R.id.dateOrder);
            total_price = itemView.findViewById(R.id.totalPriceOrder);
            name = itemView.findViewById(R.id.nameOrder);
            email = itemView.findViewById(R.id.emailOrder);
            address = itemView.findViewById(R.id.addressOrder);
            phone_number = itemView.findViewById(R.id.phonenumberOrder);
            recyclerView = itemView.findViewById(R.id.recycleOrder);
        }
    }
}
