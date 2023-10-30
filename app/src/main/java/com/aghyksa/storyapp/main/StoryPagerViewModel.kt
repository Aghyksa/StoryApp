package com.aghyksa.storyapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aghyksa.storyapp.api.StoryRepository
import com.aghyksa.storyapp.datastore.UserPreference
import com.aghyksa.storyapp.model.Story
import kotlinx.coroutines.launch

class StoryPagerViewModel(private val repository: StoryRepository): ViewModel() {
    var stories: LiveData<PagingData<Story>> = repository.getStories().cachedIn(viewModelScope)
    fun refresh(){
        stories = repository.getStories().cachedIn(viewModelScope)
    }
}