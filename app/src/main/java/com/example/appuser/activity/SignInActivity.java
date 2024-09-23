package com.example.appuser.activity;

import androidx.annotation.NonNull;
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

import com.example.appuser.R;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SignInActivity extends AppCompatActivity {
    TextInputEditText txtEmail, txtPassword;
    AppCompatButton btnSignIn;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;

    // test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
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
        String username = Paper.book().read("email");
        String pass = Paper.book().read("password");
        txtEmail.setText(username);
        txtPassword.setText(pass);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (txtEmail.getText() == null) {
                        throw new IllegalArgumentException("Do Not Left Blank Username");
                    } else if (txtPassword.getText() == null) {
                        throw new IllegalArgumentException("Do Not Left Blank Password");
                    } else {
                        String email = txtEmail.getText().toString();
                        String pass = txtPassword.getText().toString();
                        if (user != null) {
                            firebaseAuth.signOut();
                        } else if (user == null) {
                            firebaseAuth.signInWithEmailAndPassword(email, pass)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                login(email, pass);
                                                updatePass(email, pass);
                                            }
                                        }
                                    });
                        }
                    }
                } catch (IllegalArgumentException e) {
                    MotionToast.Companion.createToast(SignInActivity.this,
                            "Notice",
                            e.getMessage(),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(SignInActivity.this, R.font.helvetica_regular));
                }
            }
        });
    }

    private void initView() {
        Paper.init(getApplicationContext());
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        txtEmail = findViewById(R.id.txtEmailSI);
        txtPassword = findViewById(R.id.txtPassword);
        btnSignIn = findViewById(R.id.btnSignInNike);
        firebaseAuth =FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    public void login(String email, String password) {
        compositeDisposable.add(apiClothing.signIn(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                Paper.book().write("user_current", userModel.getResult().get(0));
                                MotionToast.Companion.createToast(SignInActivity.this,
                                        "Notice",
                                        "Sign In Success",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(SignInActivity.this, R.font.helvetica_regular));
                                Intent signIn = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(signIn);
                                finish();
                            }
                        },
                        throwable -> {
                            MotionToast.Companion.createToast(SignInActivity.this,
                                    "Notice",
                                    throwable.getMessage(),
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(SignInActivity.this, R.font.helvetica_regular));
                        }
                ));
    }
    public void updatePass(String email, String password) {
        compositeDisposable.add(apiClothing.updatePass(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }
}