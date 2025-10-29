package com.unclekostya.scaryrosiesstory.data.repository

import com.unclekostya.scaryrosiesstory.data.local.entity.*

interface StoryRepository {
    suspend fun getAllStories(): List<StoryEntity>

    suspend fun getMessageByDbId(storyId: Int, messageDbId: Int): MessageEntity?
    suspend fun getMessageByLocalId(storyId: Int, localId: Int): MessageEntity?

    suspend fun getChoicesForMessageDb(messageDbId: Int): List<ChoiceEntity>

    suspend fun getUserProgress(storyId: Int): UserProgressEntity?
    suspend fun saveProgress(progress: UserProgressEntity)
    suspend fun deleteUserProgress(storyId: Int)

    suspend fun insertStory(story: StoryEntity): Long
    suspend fun insertMessages(messages: List<MessageEntity>): List<Int> // DB ids
    suspend fun updateMessages(messages: List<MessageEntity>)
    suspend fun insertChoices(choices: List<ChoiceEntity>)
}
