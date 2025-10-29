package com.unclekostya.scaryrosiesstory.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [Index(value = ["storyId", "localId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = StoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["storyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val storyId: Int,
    val localId: Int,
    val sender: String,
    val senderImagePath: String,
    val text: String,
    val nextMessageLocalId: Int? = null
)