package com.example.wooferapp;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @Multipart
    @POST("upload.php") // Replace with your upload.php file name or endpoint
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part image,
            @Part("description") RequestBody description,
            @Query("brand") String brand,
            @Query("brand1") String brand1,
            @Query("brand2") String brand2
    );
}