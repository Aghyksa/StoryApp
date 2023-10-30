package com.aghyksa.storyapp.api

import com.aghyksa.storyapp.model.LoginResponse
import com.aghyksa.storyapp.model.RegisterResponse
import com.aghyksa.storyapp.model.StoriesResponse
import com.aghyksa.storyapp.model.StoryResponse
import com.aghyksa.storyapp.model.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Header("Authorization") token: String
    ): StoriesResponse


    @GET("stories")
    fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
        @Header("Authorization") token: String
    ): Call<StoriesResponse>

    @GET("stories/{id}")
    fun getStory(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun upload(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<UploadResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email")email:String,
        @Field("password")password:String
    ):Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name")name:String,
        @Field("email")email:String,
        @Field("password")password:String
    ): Call<RegisterResponse>
}