package com.example.appuser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.appuser.R;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class EditUserActivity extends AppCompatActivity {
    TextInputEditText firstname, lastname, address, phonenumber, email;
    AppCompatButton btnSave;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
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
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (TextUtils.isEmpty(firstname.getText())) {
                        throw new IllegalArgumentException("Do Not Left Blank First Name");
                    } else if (TextUtils.isEmpty(lastname.getText())) {
                        throw new IllegalArgumentException("Do Not Left Blank Last Name");
                    } else if (TextUtils.isEmpty(address.getText())) {
                        throw new IllegalArgumentException("Do Not Left Blank Address");
                    } else if (TextUtils.isEmpty(phonenumber.getText())) {
                        throw new IllegalArgumentException("Do Not Left Blank Phone Number");
                    } else if (TextUtils.isEmpty(email.getText())) {
                        throw new IllegalArgumentException("Do Not Left Blank Email");
                    } else {
                        compositeDisposable.add(apiClothing.updateUser(firstname.getText().toString().trim(),
                                lastname.getText().toString().trim(),
                                address.getText().toString().trim(),
                                phonenumber.getText().toString().trim(),
                                email.getText().toString().trim())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        userModel -> {
                                            if (userModel.isSuccess()) {
                                                MotionToast.Companion.createToast(EditUserActivity.this,
                                                        "Notice",
                                                        userModel.getMessage(),
                                                        MotionToastStyle.SUCCESS,
                                                        MotionToast.GRAVITY_BOTTOM,
                                                        MotionToast.SHORT_DURATION,
                                                        ResourcesCompat.getFont(EditUserActivity.this, R.font.helvetica_regular));
                                                Intent main = new Intent(EditUserActivity.this, MainActivity.class);
                                                startActivity(main);
                                                finish();
                                            }
                                        },
                                        throwable -> {
                                            MotionToast.Companion.createToast(EditUserActivity.this,
                                                    "Notice",
                                                    throwable.getMessage(),
                                                    MotionToastStyle.WARNING,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.SHORT_DURATION,
                                                    ResourcesCompat.getFont(EditUserActivity.this, R.font.helvetica_regular));
                                        }
                                ));
                    }
                } catch (IllegalArgumentException e) {
                    MotionToast.Companion.createToast(EditUserActivity.this,
                            "Notice",
                            e.getMessage(),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(EditUserActivity.this, R.font.helvetica_regular));
                }
            }
        });
    }

    private void initView() {
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        firstname = findViewById(R.id.txtFirstNameEdit);
        lastname = findViewById(R.id.txtLastNameEdit);
        address = findViewById(R.id.txtAddressEdit);
        phonenumber = findViewById(R.id.txtPhoneNumberEdit);
        email = findViewById(R.id.txtEmailEdit);
        btnSave = findViewById(R.id.btnSave);
    }
}