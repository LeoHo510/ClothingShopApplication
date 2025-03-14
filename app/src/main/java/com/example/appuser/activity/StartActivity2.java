package com.example.appuser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.appuser.R;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class StartActivity2 extends AppCompatActivity {
    ImageSlider imageSlider;
    AppCompatButton btnSignUp, btnSignIn, btnViewShop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start2);
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
        List<SlideModel> list = new ArrayList<>();
        list.add(new SlideModel(R.drawable.product_template, null));
        list.add(new SlideModel(R.drawable.product_template_2, null));
        imageSlider.setImageList(list, ScaleTypes.FIT);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signup);
                finish();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(signin);
            }
        });
        btnViewShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewShop = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(viewShop);
            }
        });
        if (Paper.book().contains("user_current")) {
            Intent main = new Intent(StartActivity2.this, MainActivity.class);
            startActivity(main);
        }
    }

    private void initView() {
        Paper.init(this);
        imageSlider = findViewById(R.id.startSlider);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnViewShop = findViewById(R.id.btnViewShop);
    }
}