package com.example.appuser.retrofit;

import com.example.appuser.model.NotiResponse;

import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNoti {
    @Headers(
            {
                    "Content-Type: application/json"
            }
    )
    @POST("/v1/projects/clothing-57842/messages:send")
    Observable<NotiResponse> sendNotification(@Body HashMap<String, Object> data);
}
