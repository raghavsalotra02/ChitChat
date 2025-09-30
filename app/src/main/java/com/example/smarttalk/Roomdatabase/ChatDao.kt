package com.example.smarttalk.Roomdatabase

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(Message : ChatEntity)

    @Query("SELECT * FROM chat_message")
    fun getAllMessages(): Flow<List<ChatEntity>>

    @Query("DELETE FROM chat_message")
    suspend fun clearChat()

}
