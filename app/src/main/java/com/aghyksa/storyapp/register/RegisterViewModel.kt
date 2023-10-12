package com.aghyksa.storyapp.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aghyksa.storyapp.api.RetrofitClient
import com.aghyksa.storyapp.model.ErrorResponse
import com.aghyksa.storyapp.model.RegisterResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterViewModel: ViewModel() {
    private val message = MutableLiveData<String>()
    fun register(name:String, email:String, password:String) {
        RetrofitClient.apiInstance
            .register(name,email,password)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful){
                        message.postValue(response.body()?.message)
                    }else{
                        val gson = Gson()
                        val error: ErrorResponse = gson.fromJson(
                            response.errorBody()!!.charStream(),
                            ErrorResponse::class.java
                        )
                        message.postValue(error.message)
                    }
                }
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    t.message?.let {
                        Log.d("Failure", it)
                        message.postValue(it)
                    }
                }
            })
    }
    fun getMessage(): LiveData<String> {
        return message
    }
}