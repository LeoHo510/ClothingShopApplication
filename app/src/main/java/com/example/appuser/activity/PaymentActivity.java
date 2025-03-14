package com.example.appuser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.appuser.R;
import com.example.appuser.model.CreateOrder;
import com.example.appuser.model.Product;
import com.example.appuser.model.User;
import com.example.appuser.model.VNPayResponse;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.ApiPushNoti;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.retrofit.RetrofitClientNoti;
import com.example.appuser.utils.AppInfo;
import com.example.appuser.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

import vn.momo.momo_partner.AppMoMoLib;

public class PaymentActivity extends AppCompatActivity {
    LinearLayoutCompat btnCash, btnZaloPay, btnMomo, btnPaypal, vnpay;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    private String amount = "10000";
    private String fee = "0";
    int environment = 0;
    private String merchantName = "";
    private String merchantCode = "MOMOC2IC220220510";
    private String description = "Pay for your order";

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
        //Momo
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

        //zalo
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.tearDown();
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);
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
                paymentSuccess(user, "Cash", "", true);
            }
        });
        vnpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createVNPayPayment();
            }
        });
        btnZaloPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestZalo(user);
            }
        });
        btnMomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMoMoPayment();
            }
        });
        btnPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    //Get token through MoMo app
    private void requestMoMoPayment() {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);

        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        eventValue.put("orderId", "orderId123456789"); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", "Mã đơn hàng"); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", fee); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId",  merchantCode+"merchant_billId_"+System.currentTimeMillis());
        eventValue.put("partnerCode", merchantCode);
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "CGV Cresent Mall");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Special");
            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
            objExtraData.put("movie_format", "2D");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventValue.put("extraData", objExtraData.toString());

        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);


    }
    //Get token callback from MoMo app an submit to server side
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if(data != null) {
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    Log.d("Thanhcong", data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                        User user = (User) getIntent().getSerializableExtra("user_info");
                        paymentSuccess(user, "momo", token, true);
                    } else {
                        Log.d("Thanhcong", "Khong thanh cong");
                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
                    Log.d("Thanhcong", message);
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    Log.d("Thanhcong", "Khong thanh cong");
                } else {
                    //TOKEN FAIL
                    Log.d("Thanhcong", "Khong thanh cong");
                }
            } else {
                Log.d("Thanhcong", "Khong thanh cong");
            }
        } else {
            Log.d("Thanhcong", "Khong thanh cong");
        }
    }
    private void requestZalo(User user) {
        CreateOrder orderApi = new CreateOrder();
        try {
            String amount = Utils.order.getTotalprice();
            double value = Double.parseDouble(amount);

            DecimalFormat df = new DecimalFormat("#"); // Định dạng không có phần thập phân
            String result = df.format(value);
            JSONObject data = orderApi.createOrder(result);

            String code = data.getString("return_code");
            Log.e("ZaloPay", "Lỗi thanh toán, return_code: " + code);
            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                if (token == null || token.isEmpty()) {
                    Log.e("ZaloPay", "Lỗi: Không lấy được token giao dịch từ ZaloPay");
                    return;
                }

                ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        paymentSuccess(user, "zalopay", token, true);
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {

                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Log.e("ZaloPay", "Lỗi thanh toán: " + zaloPayError.toString() + " - " + s + " - " + s1);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createVNPayPayment() {
        String amount = Utils.order.getTotalprice();
        double value = Double.parseDouble(amount);

        DecimalFormat df = new DecimalFormat("#"); // Định dạng không có phần thập phân
        String result = df.format(value);
        Call<VNPayResponse> call = apiClothing.createVNPayPayment(result);
        call.enqueue(new Callback<VNPayResponse>() {
            @Override
            public void onResponse(Call<VNPayResponse> call, Response<VNPayResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String paymentUrl = response.body().getData();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                    startActivity(browserIntent);
                }
            }

            @Override
            public void onFailure(Call<VNPayResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateQuantityProductFavor() {
        List<String> nameArr = new ArrayList<>();
        if (!Utils.listFavor.isEmpty()) {
            for (Product product : Utils.listFavor) {
                nameArr.add(product.getName());
            }
            Utils.listFavor = new ArrayList<>();
            Paper.book().delete("listfavor");
            for (String key : nameArr) {
                compositeDisposable.add(apiClothing.searchProduct(key)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                productModel -> {
                                    if (productModel.isSuccess()) {
                                        Utils.listFavor.add(productModel.getList().get(0));
                                    }
                                },
                                throwable -> {

                                }
                        ));
            }
            Paper.book().write("listfavor", Utils.listFavor);
        }
    }
    private void paymentSuccess(User user, String method, String token, boolean flag) {
        if (flag) {
            compositeDisposable.add(apiClothing.createOrder(user.getFirstname(), user.getLastname(), user.getAddress(), user.getPhonenumber(), user.getEmail(), Utils.user.getIduser(), Utils.listBag.size(), Utils.order.getTotalprice(), method, token, new Gson().toJson(Utils.listBag))
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
                                    updateQuantityProductFavor();
                                    pushNotiToAdmin();
                                    pushEmailToCustomer(user.getEmail(), "Order Confirmation", buildEmailMessage(user));
                                    Utils.listBag.clear();
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
    }
    private void initView() {
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        btnCash = findViewById(R.id.btnCash);
        btnZaloPay = findViewById(R.id.btnZaloPay);
        btnMomo = findViewById(R.id.btnMomo);
        btnPaypal = findViewById(R.id.btnPaypal);
        vnpay = findViewById(R.id.btnVnpay);
    }
    private void pushEmailToCustomer(String email, String subject, String message) {
        compositeDisposable.add(apiClothing.send_email_order(email, subject, message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> Log.d("", "Email sent successfully!" + email),
                        throwable -> Log.e("Email", "Error sending email: " + throwable.getMessage())
                ));
    }
    private String buildEmailMessage(User user) {
        StringBuilder message = new StringBuilder();

        message.append("<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
        message.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>");

        // Header
        message.append("<img src='").append("https://static.topcv.vn/company_logos/UVtPJzXHNpW4xuYDTIOFzTGee91V77wq_1652668489____7c7b6d92ab5b882b561d96cdff2e9a75.png").append("' alt='Product Image' style='width: 80px; height: 80px; border-radius: 5px;'>");
        message.append("<h2 style='text-align: center; color: #4CAF50;'>Thank you for your order!</h2>");
        message.append("<p>Dear ").append(user.getFirstname()).append(",</p>");
        message.append("<p>Here are the details of your order:</p>");

        // Product List
        if (!Utils.listBag.isEmpty()) {
            message.append("<ul style='list-style: none; padding: 0;'>");
            for (int i = 0; i < Utils.listBag.size(); i++) {
                String imageUrl = Utils.listBag.get(i).getUrl_img(); // Đường dẫn ảnh sản phẩm
                String productName = Utils.listBag.get(i).getName();
                productName = productName.length() > 30 ? productName.substring(0, 27) + "..." : productName;
                String size = String.valueOf(Utils.listBag.get(i).getSize());
                int quantity = Utils.listBag.get(i).getQuantity();
                String priceStr = Utils.listBag.get(i).getPrice();
                double price = (priceStr != null) ? Double.parseDouble(priceStr) * quantity : 0.0;

                message.append("<li style='margin-bottom: 20px; border-bottom: 1px solid #eee; padding-bottom: 10px;'>");
                message.append("<div style='display: flex; align-items: center;'>");
                message.append("<img src='").append(imageUrl).append("' alt='Product Image' style='width: 80px; height: 80px; border-radius: 5px; margin-right: 15px;'>");
                message.append("<div>");
                message.append("<p style='font-size: 16px; font-weight: bold; margin: 0;'>").append(productName).append("</p>");
                message.append("<p style='margin: 5px 0; font-size: 14px;'>Size: ").append(size).append("</p>");
                message.append("<p style='margin: 5px 0; font-size: 14px;'>Quantity: ").append(quantity).append("</p>");
                message.append("<p style='margin: 5px 0; font-size: 14px; color: #4CAF50; font-weight: bold;'>").append(String.format(Locale.US, "%,.0f VND", price)).append("</p>");
                message.append("</div>");
                message.append("</div>");
                message.append("</li>");
            }
            message.append("</ul>");
        } else {
            message.append("<p style='text-align: center; color: #888;'>No products found in your order.</p>");
        }

        // Total Price
        double totalPrice = (Utils.order != null) ? Double.parseDouble(Utils.order.getTotalprice()) : 0.0;
        message.append("<p style='text-align: right; font-size: 18px; font-weight: bold; margin-top: 20px;'>Total: ").append(String.format(Locale.US, "%,.0f VND", totalPrice)).append("</p>");

        // Order Status
        message.append("<p style='font-size: 16px; margin-top: 20px;'><strong>Status:</strong> Pending</p>");
        message.append("<p>We will notify you once your order is change status!</p>");

        // Footer
        message.append("<p style='text-align: center; margin-top: 30px; color: #888;'>Best regards,<br>Merchize</p>");
        message.append("</div></body></html>");

        return message.toString();
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
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}