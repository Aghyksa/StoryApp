package com.aghyksa.storyapp.utils

import com.aghyksa.storyapp.model.LoginResponse
import com.aghyksa.storyapp.model.LoginResult
import com.aghyksa.storyapp.model.StoriesResponse
import com.aghyksa.storyapp.model.Story

object DataDummy {
    fun generateDummyStories(): StoriesResponse {
        val listStory = ArrayList<Story>()
        for (i in 1..20) {
            val story = Story(
                createdAt = "2022-02-22T22:22:22Z",
                description = "Description $i",
                id = "id_$i",
                lat = i.toDouble() * 10,
                lon = i.toDouble() * 10,
                name = "Name $i",
                photoUrl = "https://akcdn.detik.net.id/visual/2020/02/14/066810fd-b6a9-451d-a7ff-11876abf22e2_169.jpeg?w=650"
            )
            listStory.add(story)
        }

        return StoriesResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = listStory
        )
    }

    fun generateDummyNoStory(): StoriesResponse {
        val listStory = ArrayList<Story>()
        return StoriesResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = listStory
        )
    }
}