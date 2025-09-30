package com.example.smarttalk.repository

import com.example.smarttalk.Model.ChatMessage
import com.example.smarttalk.Roomdatabase.ChatDao
import com.example.smarttalk.Roomdatabase.ChatEntity
import dagger.Provides
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SavedChatsRepository @Inject constructor(private val chatDao : ChatDao) {

    suspend fun insertMessage(message: ChatEntity){
        chatDao.insertMessage(message)
    }
    suspend fun getAllMessages(): Flow<List<ChatEntity>>{
        return chatDao.getAllMessages()
    }
    suspend fun clearChatHistory(){
        chatDao.clearChat()
    }
}