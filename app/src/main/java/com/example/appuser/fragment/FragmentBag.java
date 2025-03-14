package com.example.appuser.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appuser.R;
import com.example.appuser.activity.DeliveryActivity;
import com.example.appuser.activity.MainActivity;
import com.example.appuser.activity.SignInActivity;
import com.example.appuser.adapter.BagAdapter;
import com.example.appuser.model.EventBus.CheckTotalEvent;
import com.example.appuser.model.Product;
import com.example.appuser.model.ProductModel;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;
import com.google.android.material.divider.MaterialDivider;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class FragmentBag extends Fragment {
    RecyclerView recyclerView;
    TextView subtotal, shippingfee, total;
    AppCompatButton btnBag;
    BagAdapter adapter;
    LinearLayout layout, checkout;
    MaterialDivider divider;
    List<Product> list;
    double t;
    int stock;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
            checkTotal();
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(updateReceiver, new IntentFilter("update-bag-list"));
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        getData();
        checkTotal();
        btnBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnBag.getText().equals("Shop Now")) {
                    ((MainActivity) requireActivity()).switchToFragmentShop();
                } else if (btnBag.getText().equals("Checkout")) {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null || Paper.book().read("user_current") == null || Utils.user == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Notice");
                        builder.setMessage("You must login before buy something!");
                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setPositiveButton("Go to Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent login = new Intent(getContext(), SignInActivity.class);
                                startActivity(login);
                            }
                        });
                        builder.create().show();
                    } else {
                        if (isStockAvailable()) {
                            Intent delivery = new Intent(getContext(), DeliveryActivity.class);
                            startActivity(delivery);
                        } else {
                            MotionToast.Companion.createToast((Activity) requireContext(),
                                    "Notice",
                                    "Some items are out of stock!",
                                    MotionToastStyle.WARNING,
                                    MotionToast.GRAVITY_TOP,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular));
                        }
                    }
                }
            }
        });
    }

    private boolean isStockAvailable() {
        Map<Integer, Integer> productQuantityMap = getTotalQuantitiesByProductId();
        List<Integer> productIds = new ArrayList<>(productQuantityMap.keySet());
        return checkStockForAllProducts(productIds, productQuantityMap);
    }

    private Map<Integer, Integer> getTotalQuantitiesByProductId() {
        Map<Integer, Integer> productQuantityMap = new HashMap<>();

        for (Product product : Utils.listBag) {
            int productId = product.getId();
            int quantity = product.getQuantity();

            if (productQuantityMap.containsKey(productId)) {
                productQuantityMap.put(productId, productQuantityMap.get(productId) + quantity);
            } else {
                productQuantityMap.put(productId, quantity);
            }
        }

        return productQuantityMap;
    }

    private boolean checkStockForAllProducts(List<Integer> productIds, Map<Integer, Integer> productQuantityMap) {
        List<Observable<ProductModel>> observables = new ArrayList<>();
        for (int productId : productIds) {
            observables.add(apiClothing.getProductInfo(productId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
        }

        CompositeDisposable disposable = new CompositeDisposable();
        boolean[] isStockAvailable = {true};

        disposable.add(Observable.zip(observables, objects -> {
            for (int i = 0; i < objects.length; i++) {
                ProductModel productModel = (ProductModel) objects[i];
                int productId = productIds.get(i);
                int totalQuantity = productQuantityMap.get(productId);
                int availableStock = productModel.getList().get(0).getInventory_quantity();

                if (totalQuantity > availableStock) {
                    isStockAvailable[0] = false;
                }
            }
            return isStockAvailable[0];
        }).subscribe(result -> {
            if (result) {
                Intent delivery = new Intent(getContext(), DeliveryActivity.class);
                startActivity(delivery);
            } else {
                MotionToast.Companion.createToast((Activity) getContext(),
                        "Notice",
                        "Some items are out of stock!",
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_TOP,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
            }
        }, throwable -> {
            // Handle errors here
        }));

        return isStockAvailable[0];
    }
    private void initView(View v) {
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        list = new ArrayList<>();
        recyclerView = v.findViewById(R.id.bagRecyler);
        checkout = v.findViewById(R.id.checkout);
        divider = v.findViewById(R.id.dividerBag);
        subtotal = v.findViewById(R.id.subTotalBag);
        shippingfee = v.findViewById(R.id.shippingFee);
        layout = v.findViewById(R.id.baglinearlayout);
        total = v.findViewById(R.id.totalBag);
        btnBag = v.findViewById(R.id.btnShopNowBag);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        list.clear();
        adapter = new BagAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        checkTotal();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(updateReceiver);
        super.onDestroy();
    }

    public void checkTotal(){
        t = 0;
        for (int i = 0; i < Utils.listBag.size(); i++) {
            t += Double.parseDouble(Utils.listBag.get(i).getPrice()) * Utils.listBag.get(i).getQuantity();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        subtotal.setText("đ" + decimalFormat.format(t));
        total.setText("đ" + decimalFormat.format(t));
        Utils.order.setTotalprice(String.valueOf(t));
    }

    public void getData() {
        list.clear();
        list.addAll(Utils.listBag);
        adapter.setList(list);
        adapter.notifyDataSetChanged();

        if (Utils.listBag.isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            btnBag.setText("Shop Now");
            divider.setVisibility(View.GONE);
            checkout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.GONE);
            btnBag.setText("Checkout");
            divider.setVisibility(View.VISIBLE);
            checkout.setVisibility(View.VISIBLE);
            checkTotal();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventTotal(CheckTotalEvent event) {
        if (event != null) {
            checkTotal();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}
