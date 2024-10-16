package com.anubhav_auth.message_practice

import com.anubhav_auth.MessageAddedSubscription
import com.apollographql.apollo.api.ApolloResponse
import kotlinx.coroutines.flow.Flow

interface MessageClient {
    suspend fun subscribeToTopic(topic:String): Flow<Message?>
    suspend fun sendMessage(currentUserID: String, recipientUserID: String, topic :String): Message?
}