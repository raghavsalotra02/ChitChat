package com.example.smarttalk.repository

import com.example.smarttalk.Model.Message
import com.example.smarttalk.Model.User
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject
import javax.inject.Named


class FirebaseChatRepository @Inject constructor(
    @Named("usersRef")private val usersRef: DatabaseReference,
    @Named("chatsRef") private val chatsRef: DatabaseReference,
    private val contactsRef: DatabaseReference
) {

    fun addUser(userId: String, user: User) {
        usersRef.child(userId).setValue(user)
    }

    fun sendMessage(sender: String, receiver: String, messageText: String) {
        val chatId = listOf(sender, receiver).sorted().joinToString("_")
        val timestamp = System.currentTimeMillis()
        val messageId = chatsRef.child(chatId).child("messages").push().key ?: return

        val message = Message(sender, messageText, timestamp, seen = false)

        val updates = mapOf(
            "/chats/$chatId/messages/$messageId" to message,
            "/chats/$chatId/lastMessage" to mapOf("text" to messageText, "timestamp" to timestamp)
        )

        chatsRef.updateChildren(updates)
    }

    fun getMessages(sender: String, receiver: String): DatabaseReference {
        val chatId = listOf(sender, receiver).sorted().joinToString("_")
        return chatsRef.child(chatId).child("messages")
    }
}