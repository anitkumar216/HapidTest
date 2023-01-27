package com.totoinfotech.hapidtest.Interface;

import com.totoinfotech.hapidtest.Model.PojoUploadApiResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @Multipart
    @POST("PROFILE_DETAILS_ENDPOINT") // Profile details endpoint
    Call<PojoUploadApiResponse> uploadData(@Part MultipartBody.Part photo,
                                               @Part("firstName") RequestBody firstName,
                                               @Part("lastName") RequestBody lastName,
                                               @Part("phone") RequestBody phone,
                                               @Part("address") RequestBody address);

    //You can add here your project api
    /*@GET("c33e34c1-72f3-4a1f-a33d-e84c19d720a4")
    Call<PojoUploadApiResponse> getResponse();*/
}
