package com.anubhav_auth.message_practice

data class Message(
    val topic: String,
    val content: String,
    val sender: String,
)
