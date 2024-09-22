package com.example.appuser.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appuser.R;
import com.example.appuser.activity.MainActivity;
import com.example.appuser.adapter.FavorAdapter;
import com.example.appuser.model.Product;
import com.example.appuser.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class FragmentFavorite extends Fragment {
    TextView txtEdit;
    RecyclerView recyclerView;
    AppCompatButton button;
    LinearLayout layout;
    FavorAdapter adapter;
    List<Product> list;

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(updateReceiver, new IntentFilter("update-favor-list"));
    }

    private void initData() {
        getData();  // Gọi phương thức getData để cập nhật danh sách và giao diện

        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEdit.getText().equals("Edit")) {
                    txtEdit.setText("Done");
                    adapter.setEditMode(true);
                } else {
                    txtEdit.setText("Edit");
                    adapter.setEditMode(false);
                    List<Product> checkedProducts = adapter.getCheckedProducts();
                    Utils.listFavor.removeAll(checkedProducts);
                    adapter.notifyDataSetChanged();
                    getData();
                    Paper.book().write("listfavor", Utils.listFavor);

                    // Gửi broadcast thông báo danh sách yêu thích đã được cập nhật
                    Intent intent = new Intent("update-favor-list");
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)requireActivity()).switchToFragmentShop();
            }
        });
    }

    private void initView(View v) {
        list = new ArrayList<>();
        txtEdit = v.findViewById(R.id.txtEdit);
        recyclerView = v.findViewById(R.id.favorRecyler);
        button = v.findViewById(R.id.btnShopNow);
        layout = v.findViewById(R.id.linearlayout);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        list.clear();
        adapter = new FavorAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updateReceiver);
        super.onDestroy();
    }

    public void getData() {
        list.clear();
        list.addAll(Utils.listFavor);
        adapter.setList(list);
        adapter.notifyDataSetChanged();

        // Kiểm tra xem danh sách yêu thích có rỗng hay không và cập nhật giao diện
        if (Utils.listFavor.isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();  // Gọi lại phương thức getData để cập nhật giao diện và danh sách
    }
}