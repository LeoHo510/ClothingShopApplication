package com.example.appuser.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appuser.R;
import com.example.appuser.adapter.AdsAdapter;
import com.example.appuser.adapter.ProductAdapter;
import com.example.appuser.model.Product;
import com.example.appuser.model.Sales;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentHome extends Fragment {
    TextView txtHello;
    RecyclerView recyclerViewNew, recyclerViewTop;
    ImageView search;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    List<Sales> salesList;
    List<Product> productList;
    AdsAdapter adsAdapter;
    ProductAdapter productAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initData() {
        getNewProduct();
        getTopPicks();
    }
    public void getNewProduct() {
        compositeDisposable.add(apiClothing.getRandom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productModel -> {
                            if (productModel.isSuccess()) {
                                productList.clear();
                                productList.addAll(productModel.getList());
                                productAdapter.notifyDataSetChanged();
                            }
                        }
                ));
    }

    public void getTopPicks() {
        compositeDisposable.add(apiClothing.getSales()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        salesModel -> {
                            if (salesModel.isSuccess()) {
                                salesList.clear();
                                salesList.addAll(salesModel.getList());
                                adsAdapter.notifyDataSetChanged();
                            }
                        }
                ));
    }

    private void initView(View v) {
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        txtHello = v.findViewById(R.id.txtHello);
        recyclerViewTop = v.findViewById(R.id.recycleTopPicks);
        recyclerViewNew = v.findViewById(R.id.recycleNew);
        search = v.findViewById(R.id.iconSearch);
        salesList = new ArrayList<>();
        productList = new ArrayList<>();
        adsAdapter = new AdsAdapter(v.getContext(), salesList);
        productAdapter = new ProductAdapter(v.getContext(), productList);
        recyclerViewTop.setAdapter(adsAdapter);
        recyclerViewNew.setAdapter(productAdapter);
        LinearLayoutManager manager1 = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager manager2 = new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTop.setLayoutManager(manager1);
        recyclerViewTop.setHasFixedSize(true);
        recyclerViewNew.setLayoutManager(manager2);
        recyclerViewNew.setHasFixedSize(true);
    }
}
