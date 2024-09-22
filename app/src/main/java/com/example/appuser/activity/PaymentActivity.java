package com.example.appuser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.appuser.R;
import com.example.appuser.model.User;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.ApiPushNoti;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.retrofit.RetrofitClientNoti;
import com.example.appuser.utils.Utils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class PaymentActivity extends AppCompatActivity {
    LinearLayoutCompat btnCash, btnZaloPay, btnMomo, btnPaypal;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
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
        User user = (User) getIntent().getSerializableExtra("user_info");
        btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("JSON", new Gson().toJson(Utils.listBag));
                assert user != null;
                compositeDisposable.add(apiClothing.createOrder(user.getFirstname(), user.getLastname(), user.getAddress(), user.getPhonenumber(), user.getEmail(), Utils.user.getIduser(), Utils.listBag.size(), Utils.order.getTotalprice(), new Gson().toJson(Utils.listBag))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                messageModel -> {
                                    if (messageModel.isSuccess()) {
                                        for (int i = 0; i < Utils.listBag.size(); i++) {
                                            int quantity = Utils.listBag.get(i).getQuantity();
                                            compositeDisposable.add(apiClothing.getProductInfo(Utils.listBag.get(i).getId())
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(
                                                            productModel -> {
                                                                if (productModel.isSuccess()) {
                                                                    int inventory = productModel.getList().get(0).getInventory_quantity();
                                                                    int newInventory = inventory - quantity;
                                                                    updateInventoryQuantity(newInventory, productModel.getList().get(0).getId());
                                                                }
                                                            },
                                                            throwable -> {

                                                            }
                                                    ));
                                        }
                                        Utils.listBag.clear();
                                        pushNotiToAdmin();
                                        Paper.book().write("listbag", Utils.listBag);
                                        MotionToast.Companion.createToast(PaymentActivity.this,
                                                "Notice",
                                                "Success, please check your email!",
                                                MotionToastStyle.SUCCESS,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.SHORT_DURATION,
                                                ResourcesCompat.getFont(PaymentActivity.this, R.font.helvetica_regular));
                                        Intent shop = new Intent(PaymentActivity.this, MainActivity.class);
                                        startActivity(shop);
                                        finish();
                                    }
                                },
                                throwable -> {

                                }
                        ));
            }
        });
    }

    private void initView() {
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        btnCash = findViewById(R.id.btnCash);
        btnZaloPay = findViewById(R.id.btnZaloPay);
        btnMomo = findViewById(R.id.btnMomo);
        btnPaypal = findViewById(R.id.btnPaypal);
    }
    public void pushNotiToAdmin() {
        compositeDisposable.add(apiClothing.getToken(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                for (int i = 0; i < userModel.getResult().size(); i++) {
                                    Map<String, String> notification = new HashMap<>();
                                    notification.put("title", "Thông báo");
                                    notification.put("body", "Bạn có đơn hàng mới");

                                    Map<String, Object> message = new HashMap<>();
                                    message.put("token", userModel.getResult().get(i).getToken());
                                    message.put("notification", notification);

                                    HashMap<String, Object> requestBody = new HashMap<>();
                                    requestBody.put("message", message);

                                    ApiPushNoti apiPushNoti = RetrofitClientNoti.getInstance().create(ApiPushNoti.class);
                                    compositeDisposable.add(apiPushNoti.sendNotification(requestBody)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiResponse -> {
                                                        Log.d("pushNotiToUser", "Notification sent successfully: " + new Gson().toJson(notiResponse));
                                                    },
                                                    throwable -> {
                                                        Log.e("pushNotiToUser", "Error sending notification: " + throwable.getMessage(), throwable);
                                                    }
                                            ));
                                }
                            }
                        },
                        throwable -> {
                            Log.d("pushNotiToUser", throwable.getMessage());
                        }
                ));
    }

    public void updateInventoryQuantity(int inventory, int id) {
        compositeDisposable.add(apiClothing.updateInventory(id, inventory)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    messageModel -> {

                    },
                    throwable -> {

                    }
                ));
    }
}