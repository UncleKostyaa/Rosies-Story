package com.unclekostya.scaryrosiesstory.data.local.dao

import androidx.room.*
import com.unclekostya.scaryrosiesstory.data.local.entity.*

@Dao
interface StoryDao {

    @Query("SELECT * FROM stories")
    suspend fun getAllStories(): List<StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity): Long

    @Query("SELECT * FROM messages WHERE storyId = :storyId AND id = :messageDbId LIMIT 1")
    suspend fun getMessageByDbId(storyId: Int, messageDbId: Int): MessageEntity?

    @Query("SELECT * FROM messages WHERE storyId = :storyId AND localId = :localId LIMIT 1")
    suspend fun getMessageByLocalId(storyId: Int, localId: Int): MessageEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMessages(messages: List<MessageEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessages(messages: List<MessageEntity>)

    @Query("SELECT * FROM choices WHERE storyId = :storyId AND messageLocalId = :messageLocalId")
    suspend fun getChoicesForMessageLocalId(storyId: Int, messageLocalId: Int): List<ChoiceEntity>
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertChoices(choices: List<ChoiceEntity>)

    @Query("SELECT * FROM user_progress WHERE storyId = :storyId LIMIT 1")
    suspend fun getProgress(storyId: Int): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserProgressEntity)

    @Query("DELETE FROM user_progress WHERE storyId = :storyId")
    suspend fun deleteProgressByStoryId(storyId: Int)
}
