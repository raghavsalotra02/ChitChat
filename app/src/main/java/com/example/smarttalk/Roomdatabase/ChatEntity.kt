package com.example.smarttalk.Roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val message : String,
    val isUser : Boolean,
    val timeStamp : Long = System.currentTimeMillis()
)
