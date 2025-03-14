package com.example.appuser.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SignInActivity extends AppCompatActivity {
    TextInputEditText txtEmail, txtPassword;
    AppCompatButton btnSignIn;
    TextView btnForgotPassword, btnCreateAcc;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    LinearLayoutCompat btnSignUpGmail;
    TextView btnCreateUser;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;


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
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPass = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(forgotPass);
                finish();
            }
        });
        btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUp);
                finish();
            }
        });
        btnSignUpGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUp);
                finish();
            }
        });
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
                        } else {
                            firebaseAuth.signInWithEmailAndPassword(email, pass)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                if (user != null && user.isEmailVerified()) {
                                                    login(email, pass);
                                                    updatePass(email, pass);
                                                } else {
                                                    MotionToast.Companion.createToast(SignInActivity.this,
                                                            "Notice",
                                                            "Please verify your email before logging in!",
                                                            MotionToastStyle.WARNING,
                                                            MotionToast.GRAVITY_BOTTOM,
                                                            MotionToast.SHORT_DURATION,
                                                            ResourcesCompat.getFont(SignInActivity.this, R.font.helvetica_regular));
                                                }
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
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnCreateAcc = findViewById(R.id.createAcc);
        btnSignUpGmail = findViewById(R.id.btnSignUpGmail);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        txtPassword = findViewById(R.id.txtPassword);
        btnSignIn = findViewById(R.id.btnSignInNike);
        firebaseAuth =FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("702415756462-o2ol1s5qoj9470q23676cgekbce9df2n.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

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
                                Utils.user = Paper.book().read("user_current");
                                Paper.book().delete("email");
                                Paper.book().write("email", Utils.user.getEmail());
                                Paper.book().delete("password");
                                Paper.book().write("password", Utils.user.getPassword());
                                Log.d("UserCurrent", String.valueOf(Utils.user.getIduser()));
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
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            String email = user.getEmail();
                            compositeDisposable.add(apiClothing.signInWithGmail(email)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            userModel -> {
                                                if (userModel.isSuccess()) {
                                                    String pass = userModel.getResult().get(0).getPassword();
                                                    login(email, pass);
                                                    updatePass(email, pass);
                                                }
                                            }, throwable -> {
                                                MotionToast.Companion.createToast(SignInActivity.this,
                                                        "Notice",
                                                        "Đăng nhập thất bại!",
                                                        MotionToastStyle.ERROR,
                                                        MotionToast.GRAVITY_BOTTOM,
                                                        MotionToast.SHORT_DURATION,
                                                        ResourcesCompat.getFont(SignInActivity.this, R.font.helvetica_regular));
                                            }
                                    ));
                        } else {
                            MotionToast.Companion.createToast(SignInActivity.this,
                                    "Notice",
                                    "Đăng nhập thất bại!",
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(SignInActivity.this, R.font.helvetica_regular));
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Đăng nhập thất bại", e);
            }
        }
    }
}