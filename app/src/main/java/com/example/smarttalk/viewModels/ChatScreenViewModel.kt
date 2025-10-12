package com.example.smarttalk.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttalk.Model.ChatMessage
import com.example.smarttalk.Roomdatabase.ChatEntity
import com.example.smarttalk.repository.NotificationRepository
import com.example.smarttalk.repository.OpenAIRepository
import com.example.smarttalk.repository.SavedChatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val repository : OpenAIRepository,
    private val dbRepository : SavedChatsRepository,
    private val notificationRepository: NotificationRepository
): ViewModel(){

    init {
        viewModelScope.launch {
            dbRepository.getAllMessages().collect { chatEntities ->
                _messages.value = chatEntities.map { it.toChatMessage() }
            }
        }
    }

    private fun ChatEntity.toChatMessage(): ChatMessage {
        return ChatMessage(
            role = if (this.isUser) "user" else "assistant",
            content = this.message
        )
    }

    private fun ChatMessage.toChatEntity(): ChatEntity {
        return ChatEntity(
            message = this.content,
            isUser = this.role == "user"
        )
    }

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput

    fun updateUserInput(input : String){
        _userInput.value = input
    }


    fun sendMesasge(){
        val userMessage = ChatMessage("user", _userInput.value)

        _messages.value = listOf(userMessage) +  _messages.value
        _userInput.value = ""
        

        viewModelScope.launch {
            val aiResponse = repository.fetchAIResponse(userMessage.content)
            val botMessage = ChatMessage("assistant", aiResponse)

            _messages.value =  listOf(botMessage) + _messages.value
        }

    }

    fun saveChat() {
        viewModelScope.launch {

            _messages.value.forEach {
                val entity = it.toChatEntity()
                dbRepository.insertMessage(entity)
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            dbRepository.clearChatHistory()
        }
    }

}