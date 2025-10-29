package com.unclekostya.scaryrosiesstory.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.unclekostya.scaryrosiesstory.R
import com.unclekostya.scaryrosiesstory.data.local.dao.StoryDao
import com.unclekostya.scaryrosiesstory.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

@Database(
    entities = [StoryEntity::class, MessageEntity::class, UserProgressEntity::class, ChoiceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao

    companion object {
        @Volatile
        private var Instance: StoryDatabase? = null

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
            val database = StoryDatabase.getDatabase(context)
            val dao = database.storyDao()
            database.withTransaction {
                prePopulateStory(context, dao)
            }
        }
    }
}

suspend fun prePopulateStory(context: Context, storyDao: StoryDao) {
    try {
        val root = context.resources.openRawResource(R.raw.story_catalog)
            .bufferedReader().use { JSONObject(it.readText()) }

        val storiesArray = root.getJSONArray("stories")
        Log.d("DB", "Total stories: ${storiesArray.length()}")

        for (sIndex in 0 until storiesArray.length()) {
            val storyObj = storiesArray.getJSONObject(sIndex)
            val storyJson = storyObj.getJSONObject("story")
            val messagesArray = storyObj.getJSONArray("messages")
            val choicesArray = storyObj.getJSONArray("choices")

            val storyDbId = storyDao.insertStory(
                StoryEntity(
                    title = storyJson.getString("title"),
                    description = storyJson.getString("description"),
                    coverPath = storyJson.getString("coverPath")
                )
            ).toInt()

            val stage1 = buildList {
                for (i in 0 until messagesArray.length()) {
                    val m = messagesArray.getJSONObject(i)
                    add(
                        MessageEntity(
                            storyId = storyDbId,
                            localId = m.getInt("id"),
                            sender = m.getString("sender"),
                            text = m.getString("text"),
                            nextMessageDbId = null
                        )
                    )
                }
            }
            val insertedDbIds = storyDao.insertMessages(stage1)
            val localToDb = stage1.mapIndexed { idx, msg ->
                msg.localId to insertedDbIds[idx].toInt()
            }.toMap()

            val stage2 = buildList {
                for (i in 0 until messagesArray.length()) {
                    val m = messagesArray.getJSONObject(i)
                    val localId = m.getInt("id")
                    val dbId = localToDb.getValue(localId)
                    val nextLocal = m.optInt("nextMessageId", 0).takeIf { it != 0 }
                    val nextDb = nextLocal?.let { localToDb[it] }
                    add(
                        MessageEntity(
                            id = dbId,
                            storyId = storyDbId,
                            localId = localId,
                            sender = m.getString("sender"),
                            text = m.getString("text"),
                            nextMessageDbId = nextDb
                        )
                    )
                }
            }
            storyDao.updateMessages(stage2)

            val choiceEntities = buildList {
                for (i in 0 until choicesArray.length()) {
                    val c = choicesArray.getJSONObject(i)
                    val messageLocalId = c.getInt("messageId")
                    val messageDbId = localToDb.getValue(messageLocalId)
                    val nextLocal = c.optInt("nextMessageId", 0).takeIf { it != 0 }
                    val nextDb = nextLocal?.let { localToDb[it] }

                    add(
                        ChoiceEntity(
                            storyId = storyDbId,
                            messageDbId = messageDbId,
                            messageLocalId = messageLocalId,
                            text = c.getString("text"),
                            nextMessageDbId = nextDb
                        )
                    )
                }
            }
            storyDao.insertChoices(choiceEntities)

            Log.d("DB", "Inserted story#$storyDbId: ${stage1.size} messages, ${choiceEntities.size} choices")
        }
    } catch (e: Exception) {
        Log.e("DB", "Error while prepopulating DB: ${e.message}", e)
    }
}
