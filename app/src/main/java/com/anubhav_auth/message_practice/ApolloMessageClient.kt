package com.anubhav_auth.message_practice

import android.util.Log
import com.anubhav_auth.MessageAddedSubscription
import com.anubhav_auth.SendMessageMutation
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ApolloMessageClient(
    private val apolloClient: ApolloClient
) : MessageClient {

    override suspend fun subscribeToTopic(topic: String): Flow<Message?> {
        return apolloClient.subscription(MessageAddedSubscription(topic))
            .toFlow()
            .map { response ->
                Log.d("ApolloMessageClient", "Received response: $response")
                response.data?.messageAdded?.let {
                    Message(
                        topic = it.topic,
                        content = it.content,
                        sender = it.sender
                    )
                }
            }
            .catch { e ->
                Log.e("ApolloMessageClient", "Subscription error", e)
                null
            }
            .onStart { Log.d("ApolloMessageClient", "Started subscription") }
            .onCompletion { Log.d("ApolloMessageClient", "Completed subscription") }
            .onEach { message ->
                Log.d("ApolloMessageClient", "Received message: $message")
            }
    }

    override suspend fun sendMessage(
        currentUserID: String, recipientUserID: String, message: String
    ): Message? {
        val mutation = SendMessageMutation(currentUserID, recipientUserID, message)
        return apolloClient.mutation(mutation).execute().data?.sendMessage?.let {
            Message(
                topic = recipientUserID,
                content = message,
                sender = currentUserID
            )
        }

    }
}