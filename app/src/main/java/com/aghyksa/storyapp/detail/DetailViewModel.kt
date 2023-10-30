package com.aghyksa.storyapp.detail

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
import com.aghyksa.storyapp.model.StoryResponse
import com.aghyksa.storyapp.model.User
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailViewModel(private val preference: UserPreference, private val id: String): ViewModel() {
    private val message = MutableLiveData<String>()
    private val story = MutableLiveData<Story>()

    fun getUser(): LiveData<User>{
        return preference.getUser().asLiveData()
    }

    fun getMessage(): LiveData<String>{
        return message
    }

    fun getStory(): LiveData<Story>{
        return story
    }

    fun setStory(token:String) {
        RetrofitClient.apiInstance
            .getStory(id,"Bearer $token")
            .enqueue(object : Callback<StoryResponse> {
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    if (response.isSuccessful){
                        story.postValue(response.body()?.story)
                    }else{
                        val gson = Gson()
                        val error: ErrorResponse = gson.fromJson(
                            response.errorBody()!!.charStream(),
                            ErrorResponse::class.java
                        )
                        message.postValue(error.message)
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    t.message?.let {
                        Log.d("Failure", it)
                        message.postValue(it)
                    }
                }
            })
    }

}