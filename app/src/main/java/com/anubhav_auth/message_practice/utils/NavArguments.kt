package com.anubhav_auth.message_practice.utils

enum class NavArguments(private val argumentName: String) {
    HOMESCREEN("home_screen"),
    CHATSCREEN("chat_screen");

    override fun toString(): String {
        return argumentName
    }
}