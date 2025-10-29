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

    override suspend fun insertStory(story: StoryEntity) {
        return storyDao.insertStory(story = story)
    }

    override suspend fun insertMessage(message: MessageEntity) {
        return  storyDao.insertMessage(message = message)
    }

    override suspend fun insertChoices(choices: ChoiceEntity) {
        return storyDao.insertChoices(choices = choices)
    }

    override suspend fun insertUserProgress(userProgress: UserProgressEntity) {
        return storyDao.insertUserProgress(userProgress = userProgress)
    }

    override  suspend fun deleteUserProgress(storyId: Int) {
        return storyDao.deleteProgressByStoryId(storyId)
    }
}