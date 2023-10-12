package com.aghyksa.storyapp.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aghyksa.storyapp.api.RetrofitClient
import com.aghyksa.storyapp.datastore.UserPreference
import com.aghyksa.storyapp.model.ErrorResponse
import com.aghyksa.storyapp.model.StoriesResponse
import com.aghyksa.storyapp.model.Story
import com.aghyksa.storyapp.model.User
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel(private val preference: UserPreference): ViewModel() {
    private val message = MutableLiveData<String>()
    private val stories = MutableLiveData<List<Story>>()

    fun getUser(): LiveData<User>{
        return preference.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            preference.logout()
        }
    }

    fun getMessage(): LiveData<String>{
        return message
    }

    fun getStories(): LiveData<List<Story>>{
        return stories
    }

    fun setStories(token:String) {
        RetrofitClient.apiInstance
            .getStories("Bearer $token")
            .enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>
                ) {
                    if (response.isSuccessful){
                        stories.postValue(response.body()?.listStory)
                    }else{
                        val gson = Gson()
                        val error: ErrorResponse = gson.fromJson(
                            response.errorBody()!!.charStream(),
                            ErrorResponse::class.java
                        )
                        message.postValue(error.message)
                    }
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    t.message?.let {
                        Log.d("Failure", it)
                        message.postValue(it)
                    }
                }
            })
    }

}