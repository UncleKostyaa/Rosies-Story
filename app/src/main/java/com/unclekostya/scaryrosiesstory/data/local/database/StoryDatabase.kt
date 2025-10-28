package com.unclekostya.scaryrosiesstory.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unclekostya.scaryrosiesstory.R
import com.unclekostya.scaryrosiesstory.data.local.dao.StoryDao
import com.unclekostya.scaryrosiesstory.data.local.entity.ChoiceEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.MessageEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.StoryEntity
import com.unclekostya.scaryrosiesstory.data.local.entity.UserProgressEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Database(entities = [StoryEntity::class, MessageEntity::class, UserProgressEntity::class, ChoiceEntity::class], version = 1, exportSchema = false)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDao(): StoryDao

    companion object {

        @Volatile
        private var  Instance : StoryDatabase? = null

        fun getDatabase(context: Context): StoryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, StoryDatabase::class.java, "story_database")
                    .addCallback(PrepopulateRoomCallback(context))
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

class PrepopulateRoomCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            prePopulateStory(context)
        }
    }
}

suspend fun  prePopulateStory(context: Context) {
    try{
        val storyDao = StoryDatabase.getDatabase(context).storyDao()

        val storyList =
            context.resources.openRawResource(R.raw.story_catalog).bufferedReader().use {
                JSONObject(it.readText())
            }

        val storiesArray = storyList.getJSONArray("stories")

        storiesArray.takeIf { it.length() > 0 }?.let { list ->
            for (i in 0 until list.length()) {
                val storyObject = list.getJSONObject(i)
                val story = storyObject.getJSONObject("story")
                val message = storyObject.getJSONArray("messages")
                val choices = storyObject.getJSONArray("choices")

                storyDao.insertStory(StoryEntity(
                    id = story.getInt("id"),
                    title = story.getString("title"),
                    description = story.getString("description"),
                    coverPath = story.getString("coverPath")
                ))

                    message.takeIf { it.length() > 0 }?.let { list ->
                        for (i in 0 until message.length()) {
                            val msg = list.getJSONObject(i)
                            storyDao.insertMessage(
                                message = MessageEntity(
                                    Id = msg.getInt("id"),
                                    storyId = msg.getInt("storyId"),
                                    sender = msg.getString("sender"),
                                    text = msg.getString("text"),
                                    nextMessageId = msg.optInt("nextMessageId")
                                )
                            )
                        }
                    }

                    choices.takeIf { it.length() > 0 }?.let { list ->
                        for (i in 0 until choices.length()) {
                            val choice = list.getJSONObject(i)
                            storyDao.insertChoices(
                                choices = ChoiceEntity(
                                    Id = choice.getInt("id"),
                                    messageId = choice.getInt("messageId"),
                                    text = choice.getString("text"),
                                    nextMessageId = choice.getInt("nextMessageId")
                                )
                            )
                        }
                    }

            }
        }


    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}
