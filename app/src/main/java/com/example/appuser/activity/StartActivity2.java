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
    AppCompatButton btnSignUp, btnSignIn;
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
        list.add(new SlideModel("https://www.inspirationde.com/wp-content/uploads/2023/03/The-Best-Nike-Motivation-Posters-Motivate-Yourself-Just-Do.png", null));
        list.add(new SlideModel("https://i.pinimg.com/originals/1c/78/f0/1c78f09fc456ed8595da3f0fc94f2507.jpg", null));
        list.add(new SlideModel("https://mir-s3-cdn-cf.behance.net/project_modules/max_632/b0f2f847096219.586fdf02887e9.jpg", null));
        list.add(new SlideModel("https://i.pinimg.com/736x/06/ab/26/06ab2648557afa90df17a51e1ec9f960.jpg", null));
        list.add(new SlideModel("https://mir-s3-cdn-cf.behance.net/project_modules/disp/cb799b157291747.63762604ddcc3.png", null));
        list.add(new SlideModel("https://i.pinimg.com/736x/14/4c/4b/144c4bb3ad0e180a7a2be1b1daf906d0.jpg", null));
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
                finish();
            }
        });
        if (Paper.book().contains("user_current")) {
            Intent main = new Intent(StartActivity2.this, MainActivity.class);
            startActivity(main);
            finish();
        }
    }

    private void initView() {
        Paper.init(this);
        imageSlider = findViewById(R.id.startSlider);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
    }
}