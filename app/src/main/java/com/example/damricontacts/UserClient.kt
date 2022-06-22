package com.example.damricontacts

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response
import retrofit2.http.*


public interface UserClient {
  @FormUrlEncoded
  @POST("https://testapi.damri.co.id/index.php/Loginact/")
  fun requestLogin(@Field("username") username : String, @Field("password") password : String, @Field("token") token : String) : Call<UserResponse>

  @POST("https://testapi.damri.co.id/index.php/Loginact/logout/")
  fun requestLogout() : Call<UserResponse>

  @GET("https://testapi.damri.co.id/index.php/kontak/")
  fun requestContact(@Header("Cookie") cookie : String ) : Call<ContactList>

  @HTTP(method = "DELETE", path = "https://testapi.damri.co.id/index.php/kontak/", hasBody = true)
  @FormUrlEncoded
  fun deleteContact(@Header("Cookie") cookie : String, @Field("id") id : Int) : Call<UserResponse>

  @POST("https://testapi.damri.co.id/index.php/kontak/")
  @FormUrlEncoded
  fun insertContact(@Header("Cookie") cookie : String, @Field("nama") nama : String, @Field("nomor") nomor : String) : Call<UserResponse>

  @PUT("https://testapi.damri.co.id/index.php/kontak/")
  @FormUrlEncoded
  fun editContact(@Header("Cookie") cookie : String, @Field("id") id : Int, @Field("nama") nama : String, @Field("nomor") nomor : String) : Call<UserResponse>
}