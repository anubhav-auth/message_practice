package com.anubhav_auth.message_practice.data.remote

import com.anubhav_auth.message_practice.data.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageClient {
    suspend fun subscribeToTopic(topic:String): Flow<Message?>
    suspend fun sendMessage(currentUserID: String, recipientUserID: String, topic :String): Message?
}