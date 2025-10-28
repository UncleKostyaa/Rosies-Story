package com.unclekostya.scaryrosiesstory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val Id: Int = 0,
    val storyId: Int,
    val sender: String,
    val text: String,
    val nextMessageId: Int?,
)