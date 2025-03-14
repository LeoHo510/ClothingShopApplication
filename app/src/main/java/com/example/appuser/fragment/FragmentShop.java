package com.example.appuser.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appuser.R;
import com.example.appuser.activity.SearchActivity;
import com.example.appuser.adapter.ProductAdapter;
import com.example.appuser.adapter.TitleProductAdapter;
import com.example.appuser.model.Product;
import com.example.appuser.model.TitleProduct;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentShop extends Fragment {
    RecyclerView recyclerView;
    TitleProductAdapter adapter;
    Toolbar header;
    TextView headerTitle;
    ImageView btnSearch;
    List<TitleProduct> list;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Cập nhật lại danh sách sản phẩm để phản ánh trạng thái yêu thích mới
            adapter.notifyDataSetChanged();
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Paper.init(getContext());
        initView(view);
        initControl();
        if (isConnect(getContext())) {
            initData();
        }
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(updateReceiver, new IntentFilter("update-favor-list"));
    }
    private void initControl() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    headerTitle.setVisibility(View.GONE);
                } else if (dy < 0) {
                    headerTitle.setVisibility(View.VISIBLE);
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(getContext(), SearchActivity.class);
                startActivity(search);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        Context context = requireContext(); // Sử dụng requireContext để đảm bảo không bị null
        compositeDisposable.add(apiClothing.getTitleProduct()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        titleProductModel -> {
                            if (titleProductModel.isSuccess()) {
                                for (TitleProduct titleProduct : titleProductModel.getList()) {
                                    List<Product> categoryProducts = new ArrayList<>();
                                    String status = (Utils.user != null) ? String.valueOf(Utils.user.getStatus()) : "0";
                                    compositeDisposable.add(apiClothing.getProduct(titleProduct.getTitle(), Integer.parseInt(status))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    productModel -> {
                                                        if (productModel.isSuccess()) {
                                                            categoryProducts.addAll(productModel.getList());
                                                            list.add(new TitleProduct(titleProduct.getTitle(), categoryProducts));
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    },
                                                    throwable -> {
                                                        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                            ));
                                }
                            }
                        },
                        throwable -> {
                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initView(View v) {
        list = new ArrayList<>();
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        recyclerView = v.findViewById(R.id.mainRecyclerView);
        adapter = new TitleProductAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        header = v.findViewById(R.id.header);
        headerTitle = v.findViewById(R.id.header_title);
        btnSearch = v.findViewById(R.id.btnSearch);
    }

    public boolean isConnect(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected());
    }
    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updateReceiver);
        super.onDestroy();
    }
}
