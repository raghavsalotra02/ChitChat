package com.example.smarttalk.Notification

import android.annotation.SuppressLint
import com.example.smarttalk.repository.FirebaseChatRepository
import com.example.smarttalk.sharedPref.SharedPref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseChatRepository : FirebaseChatRepository
    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            notificationHelper.showNotification(
                title = it.title ?: "New Message",
                body = it.body ?: "You have a new chat"
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("token : $token")
        SharedPref.get().fcmToken = token
    }


}