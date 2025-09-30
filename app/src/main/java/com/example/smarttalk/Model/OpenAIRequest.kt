package com.example.smarttalk.Model

import com.google.gson.annotations.SerializedName


data class OpenAIRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<ChatMessage>,
    @SerializedName("max_tokens") val maxTokens: Int,
    val temperature : Float
)
