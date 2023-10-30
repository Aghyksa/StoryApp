package com.aghyksa.storyapp.model

data class StoriesResponse(
    val listStory: List<Story>,
    val error : Boolean,
    val message : String
)
