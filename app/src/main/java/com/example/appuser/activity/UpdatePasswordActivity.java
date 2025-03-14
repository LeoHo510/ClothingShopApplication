package com.example.appuser.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appuser.R;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class UpdatePasswordActivity extends AppCompatActivity {
    private TextInputEditText newPassword;
    private TextInputEditText confirmPassword;
    private TextInputEditText currentPassword;
    private Button btnUpdate, btnBack;

    private FirebaseAuth auth;

    private ProgressBar progressBar;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
    }

    private void initView() {
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmNewPassword);
        btnUpdate = findViewById(R.id.btn_update_password);
        btnBack = findViewById(R.id.btn_back_update);
        auth = FirebaseAuth.getInstance();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnUpdate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(currentPassword.getText().toString().trim()) ||
                    TextUtils.isEmpty(newPassword.getText().toString().trim()) ||
                    TextUtils.isEmpty(confirmPassword.getText().toString().trim())) {
                showToast("Please fill in all the information", MotionToastStyle.WARNING);
                return;
            }

            if (!newPassword.getText().toString().trim().equals(confirmPassword.getText().toString().trim())) {
                showToast("Passwords don't match", MotionToastStyle.INFO);
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                showToast("User not authenticated", MotionToastStyle.ERROR);
                return;
            }

            // Xác thực mật khẩu cũ trước khi thay đổi
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword.getText().toString().trim());
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Gọi API cập nhật mật khẩu trước khi đổi mật khẩu trên Firebase
                    compositeDisposable.add(apiClothing.updatePass(user.getEmail(), newPassword.getText().toString().trim())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        if (messageModel.isSuccess()) {
                                            user.updatePassword(newPassword.getText().toString().trim())
                                                    .addOnCompleteListener(updateTask -> {
                                                        if (updateTask.isSuccessful()) {
                                                            showToast("Password changed successfully", MotionToastStyle.SUCCESS);
                                                            progressBar.setVisibility(View.GONE);
                                                            finish();
                                                        } else {
                                                            progressBar.setVisibility(View.GONE);
                                                            showToast(updateTask.getException().getMessage(), MotionToastStyle.ERROR);
                                                        }
                                                    });
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            showToast("Failed to update password in server!", MotionToastStyle.ERROR);
                                        }
                                    },
                                    throwable -> {
                                        progressBar.setVisibility(View.GONE);
                                        showToast("Error: " + throwable.getMessage(), MotionToastStyle.ERROR); // Bắt lỗi RxJava
                                    }
                            ));
                } else {
                    progressBar.setVisibility(View.GONE);
                    showToast("Invalid current password!", MotionToastStyle.ERROR);
                }
            });
        });

    }

    private void showToast(String message, MotionToastStyle style) {
        MotionToast.Companion.createToast(UpdatePasswordActivity.this,
                "Thông báo",
                message,
                style,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(UpdatePasswordActivity.this, R.font.helvetica_regular));
    }
}