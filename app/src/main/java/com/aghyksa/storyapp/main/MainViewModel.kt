package com.aghyksa.storyapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aghyksa.storyapp.datastore.UserPreference
import com.aghyksa.storyapp.model.User
import kotlinx.coroutines.launch


class MainViewModel(private val preference: UserPreference): ViewModel() {
    fun getUser(): LiveData<User>{
        return preference.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            preference.logout()
        }
    }
}