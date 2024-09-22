package com.example.appuser.retrofit;

import com.example.appuser.model.MessageModel;
import com.example.appuser.model.OrderModel;
import com.example.appuser.model.ProductModel;
import com.example.appuser.model.SalesModel;
import com.example.appuser.model.TitleProductModel;
import com.example.appuser.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiClothing {
    @POST("getproduct.php")
    @FormUrlEncoded
    Observable<ProductModel> getProduct (
            @Field("category") String category,
            @Field("status") int status
    );

    @GET("gettitleproduct.php")
    Observable<TitleProductModel> getTitleProduct();

    @GET("getsales.php")
    Observable<SalesModel> getSales();

    @POST("getRandomProduct.php")
    @FormUrlEncoded
    Observable<ProductModel> getRandomProduct (
            @Field("category") String category,
            @Field("id") int id
    );

    @POST("createorder.php")
    @FormUrlEncoded
    Observable<MessageModel> createOrder(
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("address") String address,
            @Field("phonenumber") String phonenumber,
            @Field("email") String email,
            @Field("iduser") int iduser,
            @Field("quantity") int quantity,
            @Field("totalprice") String totalprice,
            @Field("details") String details
    );

    @POST("signup.php")
    @FormUrlEncoded
    Observable<UserModel> signUp(
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("address") String address,
            @Field("phonenumber") String phonenumber,
            @Field("email") String email,
            @Field("password") String password,
            @Field("uid") String uid
    );

    @POST("signin.php")
    @FormUrlEncoded
    Observable<UserModel> signIn(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken(
            @Field("iduser") int iduser,
            @Field("token") String token
    );

    @POST("updatepass.php")
    @FormUrlEncoded
    Observable<MessageModel> updatePass(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("gettoken.php")
    @FormUrlEncoded
    Observable<UserModel> getToken(
            @Field("status") int status
    );

    @POST("getorder.php")
    @FormUrlEncoded
    Observable<OrderModel> getOrder(
            @Field("iduser") int iduser
    );

    @POST("updateinventory.php")
    @FormUrlEncoded
    Observable<MessageModel> updateInventory(
            @Field("id") int id,
            @Field("inventory_quantity") int inventory_quantity
    );

    @POST("getproductinfo.php")
    @FormUrlEncoded
    Observable<ProductModel> getProductInfo(
            @Field("id") int id
    );

    @POST("search.php")
    @FormUrlEncoded
    Observable<ProductModel> searchProduct(
            @Field("key") String key
    );

    @POST("updateuser.php")
    @FormUrlEncoded
    Observable<UserModel> updateUser(
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("address") String address,
            @Field("phonenumber") String phonenumber,
            @Field("email") String email
    );

    @GET("statistics.php")
    Observable<ProductModel> getStatistics();

    @GET("getRandom.php")
    Observable<ProductModel> getRandom ();
}
