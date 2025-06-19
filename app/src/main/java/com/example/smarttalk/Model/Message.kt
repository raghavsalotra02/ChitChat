package com.example.smarttalk.Model

data class Message(
    val sender: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val seen: Boolean = false
)