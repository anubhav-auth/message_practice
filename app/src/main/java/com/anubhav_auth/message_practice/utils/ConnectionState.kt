package com.anubhav_auth.message_practice.utils

sealed class ConnectionState {
    data object Connected : ConnectionState()
    data class Disconnected(val reason:String) : ConnectionState()
    data class Error(val error: String) : ConnectionState()
}