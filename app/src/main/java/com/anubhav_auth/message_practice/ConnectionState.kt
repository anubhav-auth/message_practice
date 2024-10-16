package com.anubhav_auth.message_practice

sealed class ConnectionState {
    data object Connected: ConnectionState()
    data object Disconnected: ConnectionState()
    data object Connecting: ConnectionState()
    data object Error: ConnectionState()
}