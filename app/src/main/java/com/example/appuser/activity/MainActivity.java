package com.example.appuser.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.appuser.R;
import com.example.appuser.adapter.FragmentAdapter;
import com.example.appuser.model.EventBus.CheckTotalEvent;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    FragmentAdapter adapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        getToken();
        loadSearchString();
        loadFavorites();
        loadBag();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String string) {
                        if (!TextUtils.isEmpty(string)) {
                            compositeDisposable.add(apiClothing.updateToken(Utils.user.getIduser(), string)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe());
                        }
                    }
                });
    }

    private void loadSearchString() {
        if (Paper.book().read("searchStringList") != null) {
            Utils.stringList = new ArrayList<>();
            Utils.stringList.addAll(Paper.book().read("searchStringList"));
        } else {
            Utils.stringList = new ArrayList<>();
        }
    }

    private void loadBag() {
        if (Paper.book().read("listbag") != null) {
            Utils.listBag = new ArrayList<>();
            Utils.listBag.addAll(Paper.book().read("listbag"));
        } else {
            Utils.listBag = new ArrayList<>();
        }
    }

    private void loadFavorites() {
        if (Paper.book().read("listfavor") != null) {
            Utils.listFavor = new ArrayList<>();
            Utils.listFavor.addAll(Paper.book().read("listfavor"));
        } else {
            Utils.listFavor = new ArrayList<>();
        }
    }

    private void initView() {
        Paper.init(this);
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.navigationView);
        adapter = new FragmentAdapter(getSupportFragmentManager(), 5);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                            break;
                    case 1: bottomNavigationView.getMenu().findItem(R.id.Shop).setChecked(true);
                            break;
                    case 2: bottomNavigationView.getMenu().findItem(R.id.Favor).setChecked(true);
                            break;
                    case 3: bottomNavigationView.getMenu().findItem(R.id.Bag).setChecked(true);
                            break;
                    case 4: bottomNavigationView.getMenu().findItem(R.id.Profile).setChecked(true);
                            break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.home) {
                    viewPager.setCurrentItem(0);
                } else if (menuItem.getItemId() == R.id.Shop) {
                    viewPager.setCurrentItem(1);
                } else if (menuItem.getItemId() == R.id.Favor) {
                    viewPager.setCurrentItem(2);
                } else if (menuItem.getItemId() == R.id.Bag) {
                    viewPager.setCurrentItem(3);
                } else if (menuItem.getItemId() == R.id.Profile) {
                    viewPager.setCurrentItem(4);
                }
                return true;
            }
        });
        Utils.user = Paper.book().read("user_current");
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCheckTotalEvent(CheckTotalEvent event) {
        // Handle the event here if needed
    }

    public void switchToFragmentShop() {
        viewPager.setCurrentItem(1);
    }
}