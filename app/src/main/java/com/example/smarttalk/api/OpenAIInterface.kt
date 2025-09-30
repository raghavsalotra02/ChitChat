package com.example.smarttalk.api

import com.composeuisuite.ohteepee.BuildConfig
import com.example.smarttalk.Model.OpenAIRequest
import com.example.smarttalk.Model.OpenAIResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIInterface {
    @POST("v1/chat/completions")
    suspend fun getAIResponse(@Body request: OpenAIRequest) : OpenAIResponse
}