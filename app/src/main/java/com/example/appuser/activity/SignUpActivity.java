package com.example.appuser.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.appuser.R;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText txtFirstName, txtLastName, txtAddress, txtEmail, txtPhoneNumber, txtPassword, txtRePassword;
    AppCompatButton btnSignUp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    FirebaseAuth firebaseAuth;
    LinearLayoutCompat btnSignUpGmail;
    TextView btnLoginNow;
    private static final int RC_SIGN_IN = 123;
    private static final String PRIVATE_CLIENT_ID = "702415756462-o2ol1s5qoj9470q23676cgekbce9df2n.apps.googleusercontent.com";
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        setupGoogleSignIn();
        initView();
        initControl();
    }

    private void initControl() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String firstname = txtFirstName.getText().toString();
                    String lastname = txtLastName.getText().toString();
                    String address = txtAddress.getText().toString();
                    String email = txtEmail.getText().toString();
                    String phonenumber = txtPhoneNumber.getText().toString();
                    String password = txtPassword.getText().toString();
                    String repassword = txtRePassword.getText().toString();

                    if (firstname.isEmpty() || lastname.isEmpty() || address.isEmpty() || email.isEmpty() ||
                            phonenumber.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
                        throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin!");
                    }

                    if (!password.equals(repassword)) {
                        throw new IllegalArgumentException("Mật khẩu không trùng khớp!");
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        throw new IllegalArgumentException("Email không hợp lệ!");
                    }

                    if (password.length() < 6) {
                        throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự!");
                    }



                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        if (user != null) {
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        showToast("We've sent you an email validation to complete your account registration", MotionToastStyle.INFO);
                                                        createUser(firstname, lastname, address, phonenumber, email, password, user.getUid(), "user_pass");
                                                        firebaseAuth.signOut();
                                                        Intent login = new Intent(getApplicationContext(), SignInActivity.class);
                                                        startActivity(login);
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        // Hiển thị lỗi chi tiết
                                        Exception e = task.getException();
                                        if (e != null) {
                                            showToast("Lỗi: " + e.getMessage(), MotionToastStyle.ERROR);
                                            Log.e("FirebaseAuth", "Lỗi đăng ký", e);
                                        }
                                    }
                                }
                            });
                } catch (IllegalArgumentException e) {
                    MotionToast.Companion.createToast(SignUpActivity.this,
                            "Thông báo",
                            e.getMessage(),
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(SignUpActivity.this, R.font.helvetica_regular));
                }
            }
        });

        btnSignUpGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleSignInClient != null) {
                    mGoogleSignInClient.signOut().addOnCompleteListener(SignUpActivity.this, task -> {
                        // Sau khi đăng xuất, mới bắt đầu quá trình đăng nhập bằng Google
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    });
                }
            }
        });

        btnLoginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(login);
                finish();
            }
        });
    }

    private void initView() {
        Paper.init(getApplicationContext());
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        txtFirstName = findViewById(R.id.txtFirstNameSU);
        txtLastName = findViewById(R.id.txtLastNameSU);
        txtAddress = findViewById(R.id.txtAddressSU);
        txtEmail = findViewById(R.id.txtEmailSU);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumberSU);
        txtPassword = findViewById(R.id.txtPasswordSU);
        btnSignUp = findViewById(R.id.btnSignUpSU);
        txtRePassword = findViewById(R.id.txtRePasswordSU);
        btnSignUpGmail = findViewById(R.id.btnSignUpGmail);
        btnLoginNow = findViewById(R.id.btnLoginNow);
    }

    public void createUser(String fisrtname, String lastname, String address, String phonenumber, String email, String password, String uid, String method) {
        compositeDisposable.add(apiClothing.checkEmailDuplicate(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                compositeDisposable.add(apiClothing.signUp(fisrtname, lastname, address, phonenumber, email, password, uid)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                userModel -> {
                                                    if (userModel.isSuccess()) {
                                                        if (method.equals("email_pass")) {
                                                            Paper.book().write("email", email);
                                                            Paper.book().write("password", password);
                                                        }
                                                        MotionToast.Companion.createToast(SignUpActivity.this,
                                                                "Notice",
                                                                "Sign Up Success",
                                                                MotionToastStyle.SUCCESS,
                                                                MotionToast.GRAVITY_BOTTOM,
                                                                MotionToast.SHORT_DURATION,
                                                                ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));
                                                        Intent login = new Intent(SignUpActivity.this, SignInActivity.class);
                                                        startActivity(login);
                                                        finish();
                                                    } else {
                                                        MotionToast.Companion.createToast(SignUpActivity.this,
                                                                "Notice",
                                                                messageModel.getMessage(),
                                                                MotionToastStyle.ERROR,
                                                                MotionToast.GRAVITY_BOTTOM,
                                                                MotionToast.SHORT_DURATION,
                                                                ResourcesCompat.getFont(SignUpActivity.this, R.font.helvetica_regular));
                                                    }
                                                    Log.d("ERROR GMAIL: ", userModel.getMessage());
                                                },
                                                throwable -> {
                                                    MotionToast.Companion.createToast(SignUpActivity.this,
                                                            "Notice",
                                                            throwable.getMessage(),
                                                            MotionToastStyle.ERROR,
                                                            MotionToast.GRAVITY_BOTTOM,
                                                            MotionToast.SHORT_DURATION,
                                                            ResourcesCompat.getFont(SignUpActivity.this, R.font.helvetica_regular));
                                                }
                                        ));
                            } else {
                                MotionToast.Companion.createToast(SignUpActivity.this,
                                        "Notice",
                                        "Email already exists",
                                        MotionToastStyle.WARNING,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));
                            }
                        },
                        throwable -> {
                            MotionToast.Companion.createToast(SignUpActivity.this,
                                    "Notice",
                                    throwable.getMessage(),
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(getApplicationContext(), R.font.helvetica_regular));
                        }
                ));
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(PRIVATE_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                showToast("Đăng nhập bị hủy!", MotionToastStyle.WARNING);
                return;
            }
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account); // Kiểm tra email và xử lý
                }
            } catch (ApiException e) {
                showToast("Google Sign-In Failed!", MotionToastStyle.ERROR);
                Log.e("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.fetchSignInMethodsForEmail(account.getEmail())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        Log.e("isNewUser", String.valueOf(isNewUser));
                        if (isNewUser) {
                            // Người dùng mới, tiếp tục đăng nhập và hiển thị dialog
                            firebaseAuth.signInWithCredential(credential)
                                    .addOnCompleteListener(SignUpActivity.this, task1 -> {
                                        if (task1.isSuccessful()) {
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            if (user != null) {
                                                showInputDialog(user); // Hiển thị dialog nhập thông tin
                                            }
                                        } else {
                                            showToast("Xác thực thất bại!", MotionToastStyle.ERROR);
                                            Log.e("GoogleSignIn", "signInWithCredential:failure", task1.getException());
                                        }
                                    });
                        } else {
                            // Email đã tồn tại, dừng luồng và thông báo
                            if (mGoogleSignInClient != null) {
                                mGoogleSignInClient.signOut(); // Đăng xuất Google Sign-In
                            }
                            firebaseAuth.signOut();
                            showToast("Email đã tồn tại, vui lòng đăng nhập!", MotionToastStyle.WARNING);
                            // Quay lại màn hình đăng nhập (tùy chọn)
                            Intent login = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(login);
                            finish();
                        }
                    } else {
                        showToast("Lỗi khi kiểm tra email!", MotionToastStyle.ERROR);
                        Log.e("GoogleSignIn", "fetchSignInMethodsForEmail:failure", task.getException());
                    }
                });
    }
    private void showInputDialog(FirebaseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập thông tin người dùng");

        // Tạo Layout chứa hai EditText
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        // Tạo EditText nhập số điện thoại
        final EditText phonenumber = new EditText(this);
        phonenumber.setHint("Nhập số điện thoại");
        phonenumber.setInputType(InputType.TYPE_CLASS_PHONE);
        layout.addView(phonenumber);

        // Tạo EditText nhập địa chỉ
        final EditText address = new EditText(this);
        address.setHint("Nhập địa chỉ");
        address.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(address);

        builder.setView(layout);

        // Nút Xác nhận
        builder.setPositiveButton("OK", (dialog, which) -> {
            String userPhoneNumber = phonenumber.getText().toString().trim();
            String userAddress = address.getText().toString().trim();
            if (!userPhoneNumber.isEmpty() && !userAddress.isEmpty()) {
                createUser(user.getDisplayName(), "", userAddress, userPhoneNumber, user.getEmail(), "", user.getUid(), "gmail");
            } else {
                showToast("Vui lòng nhập dữ liệu!", MotionToastStyle.WARNING);
            }
        });

        // Nút Hủy
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }


    private void showToast(String message, MotionToastStyle style) {
        MotionToast.Companion.createToast(SignUpActivity.this,
                "Thông báo",
                message,
                style,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(SignUpActivity.this, R.font.helvetica_regular));
    }
}