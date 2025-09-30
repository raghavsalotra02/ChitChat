package com.example.smarttalk.repository

import android.adservices.adid.AdId
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.smarttalk.Model.ChatMessage
import com.example.smarttalk.Model.Message
import com.example.smarttalk.Model.User
import com.example.smarttalk.sharedPref.SharedPref
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named


class FirebaseChatRepository @Inject constructor(
    @Named("usersRef")private val usersRef: DatabaseReference,
    @Named("chatsRef") private val chatsRef: DatabaseReference,
) {

    private val _messagesList = MutableStateFlow<List<Message>>(emptyList())
    val messagesList: StateFlow<List<Message>> = _messagesList

    fun addUser(userId: String, user: User) {
        usersRef.child(userId).setValue(user)
    }

    fun fetchMatchedContacts(localContacts: List<String>, onResult: (List<Pair<String, User>>) -> Unit) {
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val matched = mutableListOf<Pair<String, User>>()
                for (child in snapshot.children) {
                    val phone = child.key
                    if (phone != null && localContacts.contains(phone)) {
                        val user = child.getValue(User::class.java)
                        user?.let { matched.add(Pair(phone, it)) }
                    }
                }
                onResult(matched)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching users", error.toException())
                onResult(emptyList())
            }
        })
    }

    suspend fun fetchMatchedContactsSuspend(localContacts: List<String>): List<Pair<String, User>> {
        return try {
            val snapshot = usersRef.get().await()
            val matched = mutableListOf<Pair<String, User>>()
            for (child in snapshot.children) {
                val phone = child.key
                if (phone != null && localContacts.contains(phone)) {
                    val user = child.getValue(User::class.java)
                    user?.let { matched.add(Pair(phone, it)) }
                }
            }
            matched
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching users", e)
            emptyList()
        }
    }

    fun getChatId(user1: String, user2: String): String {
        return if (user1 < user2) "$user1"+"_$user2" else "$user2"+"_$user1"
    }
    fun sendMessage(senderPhone: String, receiverPhone: String, messageText: String) {
        val chatId = getChatId(senderPhone, receiverPhone)
        val messagesRef = chatsRef
            .child(chatId)
            .child("messages")

        val messageId = messagesRef.push().key ?: return
        val message = Message(
            sender = senderPhone,
            text = messageText,
            timestamp = System.currentTimeMillis(),
            seen = false,
            delivered = false
        )
        messagesRef.child(messageId).setValue(message)
    }

    fun getChat( phoneNumber : String) {
        val chatId = getChatId( SharedPref.get().userPhone!! ,phoneNumber)
        //val chatRef = chatsRef.child(chatId).toString()
        val messageRef = chatsRef.child(chatId).child("messages")

        //if( chatRef  ==  chatId){
            messageRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tempList = mutableListOf<Message>()
                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(Message::class.java)
                        message?.let { tempList.add(it) }
                    }
                    _messagesList.value = tempList.reversed()
                    // Now you can use messagesList in your UI
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors here
                }
            })


        //}

    }


    suspend fun getUsersByPhones(phoneNumbers: Set<String>): Map<String, User> {
        val result = mutableMapOf<String, User>()

        for (phone in phoneNumbers) {
            val snapshot = usersRef.child(phone).get().await()
            if (snapshot.exists()) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    result[phone] = user
                }
            }
        }
        return result
    }




    suspend fun getChatPartners() : Set<String>{
        val chatPartners = mutableSetOf<String>()
        val currentUserPhone = SharedPref.get().userPhone.toString()
        val chats = chatsRef.get().await()
        for (chatSnapshot in chats.children) {
            val chatId = chatSnapshot.key ?: continue
            if (chatId.contains(currentUserPhone)) {
                val parts = chatId.split("_")
                val otherUser = if (parts[0] == currentUserPhone) parts[1] else parts[0]
                chatPartners.add(otherUser)
            }
        }
        return chatPartners
    }

}