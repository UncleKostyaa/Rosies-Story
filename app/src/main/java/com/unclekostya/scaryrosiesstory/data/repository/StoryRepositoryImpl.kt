package com.unclekostya.scaryrosiesstory.data.repository

import com.unclekostya.scaryrosiesstory.data.local.dao.StoryDao
import com.unclekostya.scaryrosiesstory.data.local.entity.*

class StoryRepositoryImpl(private val storyDao: StoryDao) : StoryRepository {

    override suspend fun getAllStories(): List<StoryEntity> =
        storyDao.getAllStories()

    override suspend fun getMessageByDbId(storyId: Int, messageDbId: Int): MessageEntity? =
        storyDao.getMessageByDbId(storyId, messageDbId)

    override suspend fun getMessageByLocalId(storyId: Int, localId: Int): MessageEntity? =
        storyDao.getMessageByLocalId(storyId, localId)

    override suspend fun getChoicesForMessageLocalId(storyId: Int, messageLocalId: Int): List<ChoiceEntity> =
        storyDao.getChoicesForMessageLocalId(storyId, messageLocalId)


    override suspend fun getUserProgress(storyId: Int): UserProgressEntity? =
        storyDao.getProgress(storyId)

    override suspend fun saveProgress(progress: UserProgressEntity) =
        storyDao.saveProgress(progress)

    override suspend fun deleteUserProgress(storyId: Int) =
        storyDao.deleteProgressByStoryId(storyId)

    override suspend fun insertStory(story: StoryEntity): Long =
        storyDao.insertStory(story)

    override suspend fun insertMessages(messages: List<MessageEntity>): List<Int> =
        storyDao.insertMessages(messages).map { it.toInt() }

    override suspend fun updateMessages(messages: List<MessageEntity>) =
        storyDao.updateMessages(messages)

    override suspend fun insertChoices(choices: List<ChoiceEntity>) =
        storyDao.insertChoices(choices)
}
