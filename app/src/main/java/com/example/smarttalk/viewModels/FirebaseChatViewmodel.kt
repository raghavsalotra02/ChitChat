package com.example.smarttalk.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarttalk.Model.Message
import com.example.smarttalk.Model.User
import com.example.smarttalk.repository.FirebaseChatRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FirebaseChatViewmodel @Inject constructor(
    private val chatRepository : FirebaseChatRepository
) : ViewModel() {


    val messagesLiveData = MutableLiveData<List<Message>>()

    fun registerUser(userId: String, name: String, profilePicUrl: String) {
        val user = User(name = name, profilePicUrl = profilePicUrl, status = "Online")
        chatRepository.addUser(userId, user)
    }

    fun sendMessage(sender: String, receiver: String, text: String) {
        chatRepository.sendMessage(sender, receiver, text)
    }

    fun observeMessages(sender: String, receiver: String) {
        val ref = chatRepository.getMessages(sender, receiver)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                snapshot.children.forEach { snap ->
                    val msg = snap.getValue(Message::class.java)
                    msg?.let { messages.add(it) }
                }
                messagesLiveData.postValue(messages)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


}