package com.anubhav_auth.message_practice.data.remote

import android.util.Log
import com.anubhav_auth.MessageAddedSubscription
import com.anubhav_auth.MessageStatusUpdateSubscription
import com.anubhav_auth.SendMessageMutation
import com.anubhav_auth.StatusUpdateMutation
import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.data.model.MessageStatusUpdates
import com.anubhav_auth.message_practice.utils.ConnectionState
import com.anubhav_auth.type.MessageStatus
import com.apollographql.apollo.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class ApolloMessageClient(
    private val apolloClient: ApolloClient
) : MessageClient {

    private val _authState =
        MutableStateFlow<ConnectionState>(ConnectionState.Disconnected(reason = "Not connected"))
    val authState = _authState.asStateFlow()

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
            _authState.emit(ConnectionState.Error(error = e.message ?: "Unknown error"))
        }.onStart {
            _authState.emit(ConnectionState.Connected)
        }.onCompletion { cause ->
            _authState.emit(
                ConnectionState.Disconnected(
                    reason = cause?.message ?: "Unknown reason"
                )
            )
        }.onEach { message ->
            Log.d("ApolloMessageClient", "Received message: $message")
        }
    }

    override suspend fun subscribeToMessageStatusUpdates(topic: String): Flow<MessageStatusUpdates?> {

        return apolloClient.subscription(MessageStatusUpdateSubscription(topic)).toFlow().map { response ->
            response.data?.messageUpdates?.let {
                MessageStatusUpdates(
                    id = it.id,
                    receiver = it.receiver,
                    status = it.status,
                    deliveredAt = it.deliveredAt,
                    readAt = it.readAt
                )
            }
        }.catch { e ->
            Log.d("ApolloMessageClient", "Error: ${e.message}")
        }.onStart {
            Log.d("ApolloMessageClient", "Connected to updates")
        }.onCompletion { cause ->
            Log.d("ApolloMessageClient", "Disconnected from updates: ${cause?.message}")
        }.onEach { messageStatusUpdates ->
            Log.d("ApolloMessageClient", "Received message status updates: $messageStatusUpdates")
        }
    }

    override suspend fun sendMessage(
        message: Message
    ): Message? {
        val mutation = SendMessageMutation(
            id = message.id,
            topic = message.topic,
            content = message.content,
            sender = message.sender,
            sentAt = message.sentAt
        )

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

    override suspend fun statusUpdate(
        message: MessageStatusUpdates,
        status: MessageStatus
    ): MessageStatusUpdates? {
        val mutation = StatusUpdateMutation(
            id = message.id,
            topic = message.receiver,
            statusUpdate = status,
            deliveredAt = message.deliveredAt,
            readAt = message.readAt
        )

        return apolloClient.mutation(mutation).execute().data?.statusUpdate?.let {
            MessageStatusUpdates(
                id = it.id,
                receiver = it.receiver,
                status = it.status,
                deliveredAt = it.deliveredAt,
                readAt = it.readAt
            )
        }
    }

}