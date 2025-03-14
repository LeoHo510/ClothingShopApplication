package com.example.appuser.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.appuser.R;
import com.example.appuser.activity.SearchActivity;
import com.example.appuser.adapter.AdsAdapter;
import com.example.appuser.adapter.CostEffectiveAdapter;
import com.example.appuser.adapter.ProductAdapter;
import com.example.appuser.model.CostEffective;
import com.example.appuser.model.Product;
import com.example.appuser.model.Ads;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentHome extends Fragment {
    TextView txtHello, vietnam, singapore, uk, us, phone, website, email, social;
    RecyclerView recyclerViewNew, recyclerViewTop;
    ImageView search;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing;
    List<Ads> adsList;
    List<Product> productList;
    AdsAdapter adsAdapter;
    ProductAdapter productAdapter;
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private int currentPosition = 0;
    ImageSlider slide;
    ListView listViewCostEffect;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initData() {
        getNewProduct();
        getTopPicks();
    }
    public void getNewProduct() {
        compositeDisposable.add(apiClothing.getRandom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productModel -> {
                            if (productModel.isSuccess()) {
                                productList.clear();
                                productList.addAll(productModel.getList());
                                productAdapter.notifyDataSetChanged();
                            }
                        }
                ));
    }

    public void getTopPicks() {
        compositeDisposable.add(apiClothing.getSales()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        salesModel -> {
                            if (salesModel.isSuccess()) {
                                adsList.clear();
                                adsList.addAll(salesModel.getList());
                                adsAdapter.notifyDataSetChanged();
                            }
                        },
                        throwable -> {
                            // Xử lý lỗi
                            Log.e("API_ERROR", "Error fetching ads", throwable);
                        }
                ));
    }

//    public void changeTexttoBold(View v) {
//        vietnam = v.findViewById(R.id.vietnamOffice);
//        SpannableString spannableString1 = new SpannableString(vietnam.getText().toString());
//        spannableString1.setSpan(new StyleSpan(Typeface.BOLD), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        vietnam.setText(spannableString1);
//        singapore = v.findViewById(R.id.singOffice);
//        SpannableString spannableString2 = new SpannableString(singapore.getText().toString());
//        spannableString2.setSpan(new StyleSpan(Typeface.BOLD), 0, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        singapore.setText(spannableString2);
//        uk = v.findViewById(R.id.ukOffice);
//        SpannableString spannableString3 = new SpannableString(uk.getText().toString());
//        spannableString3.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        uk.setText(spannableString3);
//        us = v.findViewById(R.id.usOffice);
//        SpannableString spannableString4 = new SpannableString(us.getText().toString());
//        spannableString4.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        us.setText(spannableString4);
//        phone = v.findViewById(R.id.phoneNumberFooter);
//        SpannableString spannableString5 = new SpannableString(phone.getText().toString());
//        spannableString5.setSpan(new StyleSpan(Typeface.BOLD), 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        phone.setText(spannableString5);
//        website = v.findViewById(R.id.websiteFooter);
//        SpannableString spannableString6 = new SpannableString(website.getText().toString());
//        spannableString6.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        website.setText(spannableString6);
//        email = v.findViewById(R.id.emailFooter);
//        SpannableString spannableString7 = new SpannableString(email.getText().toString());
//        spannableString7.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        email.setText(spannableString7);
//        social = v.findViewById(R.id.socialNetwork);
//        SpannableString spannableString8 = new SpannableString(social.getText().toString());
//        spannableString8.setSpan(new StyleSpan(Typeface.BOLD), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        social.setText(spannableString8);
//    }

    private void initView(View v) {
        apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);
        txtHello = v.findViewById(R.id.txtHello);
        if (!Paper.book().contains("user_current")) {
            txtHello.setText("Hello, Guest!");
        } else {
            txtHello.setText("Hello, " + Utils.user.getLastname() + " " + Utils.user.getFirstname() );
        }
        recyclerViewTop = v.findViewById(R.id.recycleTopPicks);
        recyclerViewNew = v.findViewById(R.id.recycleNew);

        //Click to search product
        search = v.findViewById(R.id.iconSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search = new Intent(getContext(), SearchActivity.class);
                startActivity(search);
            }
        });

        //Update Ads
        adsList = new ArrayList<>();
        adsAdapter = new AdsAdapter(v.getContext(), adsList);
        recyclerViewTop.setAdapter(adsAdapter);
        LinearLayoutManager manager1 = new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTop.setLayoutManager(manager1);
        recyclerViewTop.setHasFixedSize(true);
        //Auto scroll ads image
        setUpAutoScroll();

        //Update Random Product
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(v.getContext(), productList);
        recyclerViewNew.setAdapter(productAdapter);
        LinearLayoutManager manager2 = new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewNew.setLayoutManager(manager2);
        recyclerViewNew.setHasFixedSize(true);

        //slide service
        slide = v.findViewById(R.id.slide_FullfillmentService);
        List<SlideModel> listService = new ArrayList<>();
        listService.add(new SlideModel(R.drawable.slide_service_1, null));
        listService.add(new SlideModel(R.drawable.slide_service_2, null));
        slide.setImageList(listService, ScaleTypes.FIT);

        //Make text to bold style
        //changeTexttoBold(v);

        //Design Cost Effective Solutions
        listViewCostEffect = v.findViewById(R.id.listview_costeffective);
        List<CostEffective> listItems = new ArrayList<>();
        compositeDisposable.add(apiClothing.getCostEffective()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        costEffectiveModel -> {
                            if (costEffectiveModel.isSuccess()) {
                                for (int i = 0; i < costEffectiveModel.getList().size(); i++) {
                                    listItems.add(costEffectiveModel.getList().get(i));
                                }
                                if (listItems.isEmpty()) return;
                                CostEffectiveAdapter costEffectiveAdapter = new CostEffectiveAdapter(v.getContext(), (List<CostEffective>) listItems);
                                listViewCostEffect.setAdapter(costEffectiveAdapter);
                            }
                        }
                ));
    }

    private void setUpAutoScroll() {
        autoScrollHandler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (adsAdapter.getItemCount() == 0) return;
                currentPosition++;
                if (currentPosition >= adsAdapter.getItemCount()) {
                    currentPosition = 0; // Quay lại ảnh đầu tiên
                }
                recyclerViewTop.smoothScrollToPosition(currentPosition);
                autoScrollHandler.postDelayed(this, 5000);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, 5000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable); // Hủy auto-scroll khi fragment bị hủy
        }
    }
}
