package com.unclekostya.scaryrosiesstory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unclekostya.scaryrosiesstory.data.local.entity.ChoiceEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.MessageEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.StoryEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.UserProgressEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories")
    suspend fun getAllStories(): List<StoryEntity>

    @Query("SELECT * FROM messages WHERE storyId = :storyId AND id = :messageId")
    suspend fun getMessage(storyId:Int,messageId:Int): MessageEntity

    @Query("SELECT * FROM choices WHERE messageId = :messageId")
    suspend fun getChoicesForMessage(messageId: Int): List<ChoiceEntity>

    @Query("SELECT * FROM user_progress WHERE storyId = :storyId LIMIT 1")
    suspend fun getProgress(storyId: Int): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserProgressEntity)

    @Insert
    suspend fun insertStory(story: StoryEntity)

    @Insert
    suspend fun insertMessage(message: MessageEntity)

    @Insert
    suspend fun insertChoices(choices: ChoiceEntity)

    @Insert
    suspend fun insertUserProgress(userProgress: UserProgressEntity)
}