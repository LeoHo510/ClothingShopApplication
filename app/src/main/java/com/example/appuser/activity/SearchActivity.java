package com.example.appuser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.SearchView;

import com.example.appuser.R;
import com.example.appuser.adapter.StringSearchAdapter;
import com.example.appuser.model.Product;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.internal.Util;

public class SearchActivity extends AppCompatActivity {
    SearchView searchView;
    TextView btnCancel;
    RecyclerView recyclerView;
    StringSearchAdapter adapter;
    List<String> listData;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        initControl();
    }

    private void initControl() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    searchProduct("");
                } else {
                    if (!Utils.stringList.contains(query)) {
                        Utils.stringList.add(query);
                        Paper.book().write("searchStringList", Utils.stringList);
                    }
                    searchProduct(query);
                    Intent search = new Intent(getApplicationContext(), ResultSearchActivity.class);
                    search.putExtra("key", query);
                    startActivity(search);
                    finish();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    Collections.reverse(Utils.stringList);
                    listData.clear();
                    listData.addAll(Utils.stringList);
                    adapter.notifyDataSetChanged();
                } else {
                    searchProduct(newText);
                }
                return true;
            }
        });
    }

    public void searchProduct(String key) {
        compositeDisposable.add(apiClothing.searchProduct(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productModel -> {
                            if (productModel.isSuccess()) {
                                listData.clear();
                                for (Product product : productModel.getList()) {
                                    listData.add(product.getName());
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e("SearchActivity", "Search failed: " + productModel.getMessage());
                            }
                        },
                        throwable -> {
                            Log.e("SearchActivity", "Error: " + throwable.getMessage());
                        }
                ));
    }

    private void initView() {
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        listData = new ArrayList<>();
        searchView = findViewById(R.id.searchView);
        btnCancel = findViewById(R.id.btnCancel);
        recyclerView = findViewById(R.id.recyclerSearch);
        LinearLayoutManager manager = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new StringSearchAdapter(listData);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView.getQuery().length() == 0) {
            Collections.reverse(Utils.stringList);
            listData.clear();
            listData.addAll(Utils.stringList);
            adapter.notifyDataSetChanged();
        }
    }
}