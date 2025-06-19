package com.example.smarttalk.repository

import com.example.smarttalk.Model.ChatMessage
import com.example.smarttalk.Model.OpenAIRequest
import com.example.smarttalk.api.OpenAIInterface
import kotlinx.coroutines.delay
import javax.inject.Inject

class OpenAIRepository @Inject constructor(private val api : OpenAIInterface){

    suspend fun fetchAIResponse( message : String): String{
        delay(1000)
        val request = OpenAIRequest(
            model = "gpt-4o-mini",
            messages = listOf(ChatMessage("user", message)),
            maxTokens = 300,
            temperature = 0.7f
        )
        return try {
            val response = api.getAIResponse(request)
            response.choices.firstOrNull()?.message?.content ?: "No response"
        }catch (e : Exception){
            "Error : ${e.localizedMessage}"
        }
    }
}