package com.example.smarttalk.repository

import com.example.smarttalk.Model.FcmNotification
import com.example.smarttalk.Model.NotificationData
import com.example.smarttalk.api.FCMApiInterface
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val fcmApi  : FCMApiInterface){

    fun sendNotification(token : String, title : String, body : String){

        val notification = FcmNotification(
            to = token,
            notification = NotificationData(title , body)
        )

        fcmApi.sendNotification(notification = notification).enqueue(object :retrofit2.Callback<Unit>{
            override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                if (response.isSuccessful){
                    println("Notification sent successfully")
                }else{
                    println("Notification failed: ${response.errorBody()?.string()}")
                }

            }

            override fun onFailure(call: Call<Unit?>, t: Throwable) {
                println("Notification error: ${t.message}")
            }

        })

    }

}