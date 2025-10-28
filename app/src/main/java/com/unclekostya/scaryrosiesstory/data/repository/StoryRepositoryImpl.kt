package com.unclekostya.scaryrosiesstory.data.repository

import com.unclekostya.scaryrosiesstory.data.local.dao.StoryDao
import com.unclekostya.scaryrosiesstory.data.local.entity.ChoiceEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.MessageEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.StoryEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.UserProgressEntity

class StoryRepositoryImpl(private val storyDao: StoryDao) : StoryRepository {

    override suspend fun getAllStories(): List<StoryEntity> {
        return storyDao.getAllStories()
    }

    override suspend fun getMessage(
        storyId: Int,
        messageId: Int
    ): MessageEntity {
        return storyDao.getMessage(
            storyId = storyId,
            messageId = messageId
        )
    }

    override suspend fun getChoicesForMessage(messageId: Int): List<ChoiceEntity> {
        return storyDao.getChoicesForMessage(messageId = messageId)
    }

    override suspend fun getUserProgress(storyId: Int): UserProgressEntity? {
        return storyDao.getProgress(storyId = storyId)
    }

    override suspend fun saveProgress(progress: UserProgressEntity) {
        return  storyDao.saveProgress(progress = progress)
    }
}