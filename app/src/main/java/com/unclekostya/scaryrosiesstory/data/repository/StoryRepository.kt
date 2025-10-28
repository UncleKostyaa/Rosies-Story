package com.unclekostya.scaryrosiesstory.data.repository

import android.os.Message
import com.unclekostya.scaryrosiesstory.data.local.entity.ChoiceEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.MessageEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.StoryEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.UserProgressEntity

interface StoryRepository {

    suspend fun getAllStories(): List<StoryEntity>

    suspend fun getMessage(storyId: Int, messageId: Int): MessageEntity

    suspend fun getChoicesForMessage(messageId: Int): List<ChoiceEntity>

    suspend fun getUserProgress(storyId: Int): UserProgressEntity?

    suspend fun saveProgress(progress: UserProgressEntity)
}