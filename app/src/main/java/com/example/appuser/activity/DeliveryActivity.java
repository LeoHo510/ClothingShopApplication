package com.example.appuser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.appuser.R;
import com.example.appuser.model.User;
import com.example.appuser.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class DeliveryActivity extends AppCompatActivity {
    TextInputEditText txtFirstName, txtLastName, txtAddress, txtEmail, txtPhoneNumber;
    AppCompatButton btnContinue;
    CheckBox checkBoxDL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
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
        try {
            txtFirstName.setText(Utils.user.getFirstname());
            txtLastName.setText(Utils.user.getLastname());
            txtEmail.setText(Utils.user.getEmail());
            txtAddress.setText(Utils.user.getAddress());
            txtPhoneNumber.setText(Utils.user.getPhonenumber());
            if (txtFirstName.getText() == null) {
                throw new IllegalArgumentException("Do not left blank Firstname!");
            } else if (txtLastName.getText() == null) {
                throw new IllegalArgumentException("Do not left blank Lastname!");
            } else if (txtAddress.getText() == null) {
                throw new IllegalArgumentException("Do not left blank Address!");
            } else if (txtPhoneNumber.getText() == null) {
                throw new IllegalArgumentException("Do not left blank Phone Number!");
            } else if (txtEmail.getText() == null) {
                throw new IllegalArgumentException("Do not left blank Email!");
            } else {
                checkBoxDL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            btnContinue.setEnabled(true);
                            btnContinue.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    User user = new User();
                                    user.setFirstname(txtFirstName.getText().toString());
                                    user.setLastname(txtLastName.getText().toString());
                                    user.setEmail(txtEmail.getText().toString());
                                    user.setAddress(txtAddress.getText().toString());
                                    user.setPhonenumber(txtPhoneNumber.getText().toString());
                                    Intent payment = new Intent(getApplicationContext(), PaymentActivity.class);
                                    payment.putExtra("user_info", user);
                                    startActivity(payment);
                                    finish();
                                }
                            });
                        } else {
                            btnContinue.setEnabled(false);
                        }
                    }
                });
            }
        } catch (IllegalArgumentException e) {
            MotionToast.Companion.createToast(this,
                    "Notice",
                    e.getMessage(),
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_CENTER,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular));
        }
    }

    private void initView() {
        checkBoxDL = findViewById(R.id.checkboxDL);
        btnContinue = findViewById(R.id.btnContinue);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtAddress = findViewById(R.id.txtAddress);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
    }
}