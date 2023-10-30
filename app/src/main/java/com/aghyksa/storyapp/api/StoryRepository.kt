package com.aghyksa.storyapp.api

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.aghyksa.storyapp.model.Story

class StoryRepository(
    private val apiService: Api,
    private val token: String
) {
    fun getStories(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService,token)
            }
        ).liveData
    }
}