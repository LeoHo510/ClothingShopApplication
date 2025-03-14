package com.example.appuser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.appuser.R;
import com.example.appuser.model.Product;
import com.example.appuser.model.TitleProduct;
import com.example.appuser.retrofit.ApiClothing;
import com.example.appuser.retrofit.RetrofitClient;
import com.example.appuser.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class  TitleProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<TitleProduct> list;
    List<Product> productList;
    ProductAdapter adapter1;
    ProductAdapter adapter2;
    private final static int VIEW_TYPE_PRODUCT = 0;
    private final static int VIEW_TYPE_ADS = 1;
    private final static int VIEW_TYPE_PRODUCT_FLASH_SALE = 2;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiClothing apiClothing = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiClothing.class);

    public TitleProductAdapter(Context context, List<TitleProduct> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PRODUCT) {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_parent_adapter, parent, false));
        } else if(viewType == VIEW_TYPE_ADS) {
            return new AdsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_parent_ads, parent, false));
        } else {
            return new FlashSaleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_parent_flashsales, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            TitleProduct titleProduct = list.get(position);
            if (titleProduct != null) {
                myViewHolder.parentTitle.setText(titleProduct.getTitle());
                adapter1 = new ProductAdapter(context, titleProduct.getList());
                myViewHolder.recyclerView.setAdapter(adapter1);
                LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                myViewHolder.recyclerView.setLayoutManager(manager);
                myViewHolder.recyclerView.setHasFixedSize(true);
            }
        } else if (holder instanceof AdsViewHolder) {
            AdsViewHolder adsViewHolder = (AdsViewHolder) holder;
            adsViewHolder.titleADS.setText("Sales");
            List<SlideModel> list = new ArrayList<>();
            compositeDisposable.add(apiClothing.getSales()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            salesModel -> {
                                if (salesModel.isSuccess()) {
                                    for (int i = 0; i < salesModel.getList().size(); i++) {
                                        list.add(new SlideModel(salesModel.getList().get(i).getUrl(), null));
                                    }
                                    adsViewHolder.imageSlider.setImageList(list, ScaleTypes.CENTER_INSIDE);
                                }
                            },
                            throwable -> {
                                Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    ));
        } else {
            productList = new ArrayList<>();
            FlashSaleViewHolder flashSaleViewHolder = (FlashSaleViewHolder) holder;
            flashSaleViewHolder.recyclerView.setHasFixedSize(true);
            LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            flashSaleViewHolder.recyclerView.setLayoutManager(manager);
            adapter2 = new ProductAdapter(context, productList);
            flashSaleViewHolder.recyclerView.setAdapter(adapter2);
            compositeDisposable.add(apiClothing.getStatistics()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            productModel -> {
                                if (productModel.isSuccess()) {
                                    if (!productModel.getList().isEmpty()) {
                                        productList.clear();
                                        productList.addAll(productModel.getList());
                                        adapter2.notifyDataSetChanged();
                                        flashSaleViewHolder.recyclerView.setVisibility(View.VISIBLE);
                                    } else {
                                        flashSaleViewHolder.recyclerView.setVisibility(View.GONE);
                                    }
                                }
                            },
                            throwable -> {
                                Toast.makeText(context, "Lỗi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                                flashSaleViewHolder.recyclerView.setVisibility(View.GONE);
                            }
                    ));

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getTitle().equalsIgnoreCase("Sales")) {
            return VIEW_TYPE_ADS;
        } else if (list.get(position).getTitle().equalsIgnoreCase("Flash Sales")) {
            return VIEW_TYPE_PRODUCT_FLASH_SALE;
        } else {
            return VIEW_TYPE_PRODUCT;
        }
    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    class AdsViewHolder extends RecyclerView.ViewHolder {
        TextView titleADS;
        ImageSlider imageSlider;
        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleADS = itemView.findViewById(R.id.parentTitleADS);
            imageSlider = itemView.findViewById(R.id.image_slider_ads);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView parentTitle;
        RecyclerView recyclerView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            parentTitle = itemView.findViewById(R.id.parentTitle);
            recyclerView = itemView.findViewById(R.id.recyclerviewParent);
        }
    }

    class FlashSaleViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public FlashSaleViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerFlashSale);
        }
    }
}
