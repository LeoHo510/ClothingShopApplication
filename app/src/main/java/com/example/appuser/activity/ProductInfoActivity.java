package com.example.appuser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appuser.R;
import com.example.appuser.adapter.ProductAdapter;
import com.example.appuser.model.EventBus.CheckTotalEvent;
import com.example.appuser.model.Product;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ProductInfoActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView nameHeader, nameitem, priceitem, infoitem, inventoryQuantity;
    ImageView btnSearch, imageItem;
    AppCompatButton btnSize, btnAddTobag, btnFavor;
    RecyclerView recyclerView;
    ProductAdapter adapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    List<Product> list;
    Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        getToolbarSupport();
        initData();
    }

    public void getRandomProduct(String category, int id) {
        compositeDisposable.add(apiClothing.getRandomProduct(category, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productModel -> {
                            if (productModel.isSuccess()) {
                                list.addAll(productModel.getList());
                                adapter.notifyDataSetChanged();
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void initData() {
        product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            nameHeader.setText(product.getName());
            nameitem.setText(product.getName());
            DecimalFormat decimalFormat = new DecimalFormat("đ###,###,###");
            priceitem.setText(decimalFormat.format(Double.parseDouble(product.getPrice())));
            infoitem.setText(product.getInfo());
            inventoryQuantity.setText("Inventory Quantity: " + product.getInventory_quantity());
            Glide.with(getApplicationContext()).load(product.getUrl_img()).into(imageItem);
            getRandomProduct(product.getCategory(), product.getId());
            if (product.getInventory_quantity() == 0) {
                btnSize.setEnabled(false);
                btnAddTobag.setEnabled(false);
            } else {
                btnSize.setEnabled(true);
                btnAddTobag.setEnabled(true);
            }
            btnSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomDialog();
                }
            });
            btnAddTobag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (product.getSize() == 0) {
                        MotionToast.Companion.createToast(ProductInfoActivity.this,
                                "Notice",
                                "Plese Select Size Before Add This Product Into Bag Shop",
                                MotionToastStyle.WARNING,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(ProductInfoActivity.this, R.font.helvetica_regular));
                    } else {
                        boolean isProductInBag = false;

                        for (int i = 0; i < Utils.listBag.size(); i++) {
                            Product p = Utils.listBag.get(i);
                            if (p.getId() == product.getId() && p.getSize() == product.getSize()) {
                                int quantity = p.getQuantity();
                                quantity += 1;
                                if (quantity > p.getInventory_quantity()) {
                                    MotionToast.Companion.createToast(ProductInfoActivity.this,
                                            "Notice",
                                            "Over Inventory!",
                                            MotionToastStyle.WARNING,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.SHORT_DURATION,
                                            ResourcesCompat.getFont(ProductInfoActivity.this, R.font.helvetica_regular));
                                } else {
                                    p.setQuantity(quantity);
                                    isProductInBag = true;
                                    break;
                                }
                            }
                        }

                        if (!isProductInBag) {
                            Product newProduct = new Product();
                            newProduct.setId(product.getId());
                            newProduct.setName(product.getName());
                            newProduct.setPrice(product.getPrice());
                            newProduct.setUrl_img(product.getUrl_img());
                            newProduct.setInfo(product.getInfo());
                            newProduct.setCategory(product.getCategory());
                            newProduct.setSize(product.getSize());
                            newProduct.setQuantity(1);
                            newProduct.setInventory_quantity(product.getInventory_quantity());
                            if (newProduct.getQuantity() > newProduct.getInventory_quantity()) {
                                MotionToast.Companion.createToast(ProductInfoActivity.this,
                                        "Notice",
                                        "Over Inventory!",
                                        MotionToastStyle.WARNING,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(ProductInfoActivity.this, R.font.helvetica_regular));
                            } else {
                                Utils.listBag.add(newProduct);
                            }
                        }
                        btnSize.setText("SELECT SIZE");
                        MotionToast.Companion.createToast(ProductInfoActivity.this,
                                "Notice",
                                "Add To  Bag Shop Successful!",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(ProductInfoActivity.this, R.font.helvetica_regular));
                        adapter.notifyDataSetChanged();
                        sendUpdateBroadcast();
                        EventBus.getDefault().postSticky(new CheckTotalEvent());
                    }
                }
            });
            btnFavor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.listFavor.contains(product)) {
                        Utils.listFavor.add(product);
                        adapter.notifyDataSetChanged();
                        sendUpdateFavorBroadcast();
                        MotionToast.Companion.createToast(ProductInfoActivity.this,
                                "Notice",
                                "Add Product to Favor List Success!",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(ProductInfoActivity.this, R.font.helvetica_regular));
                    }
                }
            });
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent search = new Intent(ProductInfoActivity.this, SearchActivity.class);
                    startActivity(search);
                }
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    private void showBottomDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        String[] sizes = getResources().getStringArray(R.array.size_array);
        for (String size : sizes) {
            TextView item = new TextView(this);
            item.setText(size);
            item.setPadding(20, 20, 20, 20);
            item.setTextSize(18);
            item.setTextColor(R.color.black);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    product.setSize(Integer.parseInt(size));
                    bottomSheetDialog.dismiss();
                    btnSize.setText(size);
                }
            });
            ((ViewGroup) bottomSheetView).addView(item);
        }
        bottomSheetDialog.show();
    }

    private void sendUpdateBroadcast() {
        Intent intent = new Intent("update-bag-list");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Paper.book().write("listbag", Utils.listBag);
    }

    private void sendUpdateFavorBroadcast() {
        Intent intent = new Intent("update-favor-list");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Paper.book().write("listfavor", Utils.listFavor);
    }


    private void getToolbarSupport() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Tắt tiêu đề
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        list = new ArrayList<>();
        toolbar = findViewById(R.id.itemToolbar);
        nameitem = findViewById(R.id.nameitem);
        nameHeader = findViewById(R.id.itemnameHeader);
        priceitem = findViewById(R.id.priceitem);
        inventoryQuantity = findViewById(R.id.inventoryQuantity);
        infoitem = findViewById(R.id.infoitem);
        btnSearch = findViewById(R.id.btnSearchItem);
        imageItem = findViewById(R.id.imageItem);
        btnSize = findViewById(R.id.btnSelectSize);
        btnAddTobag = findViewById(R.id.btnAddTobag);
        btnFavor = findViewById(R.id.btnFavorite);
        recyclerView = findViewById(R.id.itemRecycler);
        adapter = new ProductAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}