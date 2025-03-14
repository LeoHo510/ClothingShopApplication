package com.example.appuser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appuser.R;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {
    List<String> list;
    Context context;

    public ContentAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ContentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_content, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContentAdapter.MyViewHolder holder, int position) {
        String content = list.get(position);
        if (content != null) {
            holder.content.setText(content);
        } else {
            holder.content.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.item_content);
        }
    }
}
