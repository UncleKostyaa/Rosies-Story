package com.unclekostya.scaryrosiesstory.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "choices",
    indices = [Index(value = ["storyId", "messageLocalId"])],
    foreignKeys = [
        ForeignKey(
            entity = StoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["storyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val storyId: Int,
    val messageLocalId: Int,
    val text: String,
    val nextMessageLocalId: Int? = null
)