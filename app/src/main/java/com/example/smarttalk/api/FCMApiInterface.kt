package com.example.smarttalk.api

import com.example.smarttalk.Model.FcmNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMApiInterface {

    @Headers(
        "Content-Type: application/json",
        "Authorization: key=YOUR_SERVER_KEY_HERE"
    )
    @POST("fcm/send")
    fun sendNotification(@Body notification: FcmNotification): Call<Unit>
}
