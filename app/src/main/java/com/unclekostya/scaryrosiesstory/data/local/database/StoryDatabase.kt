package com.unclekostya.scaryrosiesstory.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unclekostya.scaryrosiesstory.data.local.dao.StoryDao
import com.unclekostya.scaryrosiesstory.data.local.entity.ChoiceEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.MessageEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.StoryEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.UserProgressEntity

@Database(entities = [StoryEntity::class, MessageEntity::class, UserProgressEntity::class, ChoiceEntity::class], version = 1, exportSchema = false)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDao(): StoryDao

    companion object {

        @Volatile
        private var  Instance : StoryDatabase? = null

        fun getDatabase(context: Context): StoryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, StoryDatabase::class.java, "story_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}