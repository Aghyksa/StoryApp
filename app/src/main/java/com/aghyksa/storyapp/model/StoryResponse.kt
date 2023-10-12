package com.aghyksa.storyapp.model

data class StoryResponse(
    val story: Story,
    val error : Boolean,
    val message : String
)
