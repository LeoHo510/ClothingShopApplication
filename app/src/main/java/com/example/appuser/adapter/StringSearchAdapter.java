package com.example.appuser.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appuser.Interface.ItemClickListener;
import com.example.appuser.R;
import com.example.appuser.activity.ResultSearchActivity;
import com.example.appuser.utils.Utils;

import java.util.List;

import io.paperdb.Paper;

public class StringSearchAdapter extends RecyclerView.Adapter<StringSearchAdapter.MyViewHolder> {
    private List<String> listData;

    public StringSearchAdapter(List<String> listData) {
        this.listData = listData;
    }

    public void setListData(List<String> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public StringSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.string_search_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StringSearchAdapter.MyViewHolder holder, int position) {
        if (!listData.isEmpty()) {
            holder.itemText.setText(listData.get(position));
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    if (!Utils.stringList.contains(listData.get(position))) {
                        Utils.stringList.add(listData.get(position));
                        Paper.book().write("searchStringList", Utils.stringList);
                    }
                    Intent search = new Intent(view.getContext(), ResultSearchActivity.class);
                    search.putExtra("key", listData.get(position));
                    view.getContext().startActivity(search);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listData.isEmpty() ? 0 : listData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemText;
        ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.item_text);
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
