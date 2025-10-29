package com.unclekostya.scaryrosiesstory.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_progress",
    foreignKeys = [
        ForeignKey(
            entity = StoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["storyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["currentMessageDbId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ChoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["lastChoiceDbId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("storyId"), Index("currentMessageDbId"), Index("lastChoiceDbId")]
)
data class UserProgressEntity(
    @PrimaryKey val storyId: Int,
    val currentMessageDbId: Int?,
    val lastChoiceDbId: Int?
)