package com.aghyksa.storyapp.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.aghyksa.storyapp.api.RetrofitClient
import com.aghyksa.storyapp.datastore.UserPreference
import com.aghyksa.storyapp.model.ErrorResponse
import com.aghyksa.storyapp.model.UploadResponse
import com.aghyksa.storyapp.model.User
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class AddViewModel(private val preference: UserPreference): ViewModel() {
    private val message = MutableLiveData<String>()
    private val uploadResponse = MutableLiveData<UploadResponse>()
    fun getUser(): LiveData<User>{
        return preference.getUser().asLiveData()
    }
    fun getUploadResponse(): LiveData<UploadResponse>{
        return uploadResponse
    }
    fun getMessage(): LiveData<String>{
        return message
    }

    fun upload(token:String, image: File, desc:String) {
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            image.name,
            requestImageFile)
        RetrofitClient.apiInstance
            .upload("Bearer $token",imageMultipart ,desc.toRequestBody("text/plain".toMediaType()))
            .enqueue(object : Callback<UploadResponse> {
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {
                    if (response.isSuccessful){
                        uploadResponse.postValue(response.body())
                    }else{
                        val gson = Gson()
                        val error: ErrorResponse = gson.fromJson(
                            response.errorBody()!!.charStream(),
                            ErrorResponse::class.java
                        )
                        message.postValue(error.message)
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    t.message?.let {
                        Log.d("Failure", it)
                        message.postValue(it)
                    }
                }
            })
    }

}