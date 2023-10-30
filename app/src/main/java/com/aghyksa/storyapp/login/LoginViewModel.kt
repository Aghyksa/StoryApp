package com.aghyksa.storyapp.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aghyksa.storyapp.api.RetrofitClient
import com.aghyksa.storyapp.datastore.UserPreference
import com.aghyksa.storyapp.model.ErrorResponse
import com.aghyksa.storyapp.model.LoginResponse
import com.aghyksa.storyapp.model.User
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(private val preference: UserPreference): ViewModel() {
    private val message = MutableLiveData<String>()
    fun getUser(): LiveData<User>{
        return preference.getUser().asLiveData()
    }
    fun setUser(userData: User){
        viewModelScope.launch {
            preference.setUser(userData)
        }
    }
    fun login(email:String, password:String) {
        RetrofitClient.apiInstance
            .login(email,password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful){
                        setUser(User(response.body()?.loginResult!!.token,true))
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
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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