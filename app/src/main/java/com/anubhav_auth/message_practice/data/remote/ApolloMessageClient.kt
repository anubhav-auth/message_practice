package com.anubhav_auth.message_practice.data.remote

import android.util.Log
import com.anubhav_auth.MessageAddedSubscription
import com.anubhav_auth.SendMessageMutation
import com.anubhav_auth.message_practice.data.model.Message
import com.apollographql.apollo.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.time.LocalDateTime
import java.util.UUID

class ApolloMessageClient(
    private val apolloClient: ApolloClient
) : MessageClient {

    override suspend fun subscribeToTopic(topic: String): Flow<Message?> {
        return apolloClient.subscription(MessageAddedSubscription(topic)).toFlow().map { response ->
                response.data?.messageAdded?.let {
                    Message(
                        id = it.id,
                        topic = it.topic,
                        content = it.content,
                        sender = it.sender,
                        receiver = it.receiver,
                        status = it.status,
                        sentAt = it.sentAt,
                        deliveredAt = it.deliveredAt,
                        readAt = it.readAt
                    )
                }
            }.catch { e ->
                Log.e("ApolloMessageClient", "Subscription error", e)
                null
            }.onStart { Log.d("ApolloMessageClient", "Started subscription $topic") }
            .onCompletion {cause->
                if (cause!=null) {
                    Log.d("ApolloMessageClient", "Completed subscription cause: ${cause.message}")
                }
                else{
                    Log.d("ApolloMessageClient", "Completed subscription normally")
                    }
            }
            .onEach { message ->
                Log.d("ApolloMessageClient", "Received message: $message")
            }
    }

    override suspend fun sendMessage(
        topic: String, sender: String, message: String
    ): Message? {
        val id = "${LocalDateTime.now()}_${UUID.randomUUID()}"
        val mutation = SendMessageMutation(id = id, topic = topic, content = message, sender = sender)
        return apolloClient.mutation(mutation).execute().data?.sendMessage?.let {
            Message(
                id = it.id,
                topic = it.topic,
                content = it.content,
                sender = it.sender,
                receiver = it.receiver,
                status = it.status,
                sentAt = it.sentAt,
                deliveredAt = it.deliveredAt,
                readAt = it.readAt
            )
        }
    }
}