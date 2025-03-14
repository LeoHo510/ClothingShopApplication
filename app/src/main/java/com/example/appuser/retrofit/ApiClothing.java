package com.example.appuser.retrofit;

import com.example.appuser.model.CostEffective;
import com.example.appuser.model.CostEffectiveModel;
import com.example.appuser.model.MessageModel;
import com.example.appuser.model.OrderModel;
import com.example.appuser.model.ProductModel;
import com.example.appuser.model.AdsModel;
import com.example.appuser.model.TitleProductModel;
import com.example.appuser.model.UserModel;
import com.example.appuser.model.VNPayResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
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

    @GET("getcosteffective.php")
    Observable<CostEffectiveModel> getCostEffective();

    @GET("getsales.php")
    Observable<AdsModel> getSales();

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
            @Field("method") String method,
            @Field("token") String token,
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

    @POST("checkEmailDuplicate.php")
    @FormUrlEncoded
    Observable<MessageModel> checkEmailDuplicate(
            @Field("email") String email
    );

    @POST("signInWithGmail.php")
    @FormUrlEncoded
    Observable<UserModel> signInWithGmail(
            @Field("email") String email
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

    @POST("send_email.php")
    @FormUrlEncoded
    Observable<MessageModel> send_email_order(
            @Field("email") String email,
            @Field("subject") String subject,
            @Field("message") String message
    );

    @POST("create_payment.php")
    @FormUrlEncoded
    Call<VNPayResponse> createVNPayPayment(
            @Field("amount") String amount
    );


    @GET("statistics.php")
    Observable<ProductModel> getStatistics();

    @GET("getRandom.php")
    Observable<ProductModel> getRandom ();
}
