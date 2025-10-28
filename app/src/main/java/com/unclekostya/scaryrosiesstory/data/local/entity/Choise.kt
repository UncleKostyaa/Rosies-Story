package com.unclekostya.scaryrosiesstory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "choices")
data class ChoiceEntity(
    @PrimaryKey(autoGenerate = true) val Id: Int = 0,
    val messageId: Int,
    val text: String,
    val nextMessageId: Int
)
