package com.anubhav_auth.message_practice.data.repository

import com.anubhav_auth.message_practice.data.local.MessagesDAO
import com.anubhav_auth.message_practice.data.local.TopicsSubscribedDAO
import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.data.model.TopicsSubscribed
import com.anubhav_auth.message_practice.data.remote.ApolloMessageClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val apolloMessageClient: ApolloMessageClient,
    private val messagesDAO: MessagesDAO,
    private val topicsSubscribedDAO: TopicsSubscribedDAO
) {

    suspend fun sendMessage(topic: String, sender: String, message: String): Message? {

        val messageFromServer =
            apolloMessageClient.sendMessage(topic = topic, sender = sender, message = message)
        messageFromServer?.let {
            messagesDAO.upsertMessage(it)
        }
        return messageFromServer
    }

    suspend fun subscribeToTopic(topic: String): Flow<Message?> {
        return channelFlow {
            apolloMessageClient.subscribeToTopic(topic).collectLatest { message ->
                message?.let {
                    messagesDAO.upsertMessage(it)
                    send(it)
                }
            }
        }
    }

    suspend fun deleteMessage(message: Message) {
        messagesDAO.deleteMessage(message)
    }

    fun getMessageBetweenUsers(
        loggedInUserId: String,
        chatPartnerID: String
    ): Flow<List<Message>> {
        return messagesDAO.getAllMessagesBetweenUsers(loggedInUserId, chatPartnerID)
    }

    fun getLastMessageBetweenUsers(
        loggedInUserId: String,
        chatPartnerID: String
    ): Flow<Message?> {
        return messagesDAO.getLastMessageBetweenUsers(loggedInUserId, chatPartnerID)
    }

    suspend fun getAllUniqueSenders(): List<String> {
        return messagesDAO.getAllUniqueSenders()
    }

    suspend fun upsertTopic(topic: String) {
        topicsSubscribedDAO.upsertTopic(TopicsSubscribed(topic))
    }

    suspend fun deleteTopic(topic: String) {
        topicsSubscribedDAO.deleteTopic(TopicsSubscribed(topic))
    }

    suspend fun getAllTopics(): List<TopicsSubscribed> {
        return topicsSubscribedDAO.getAllTopics()
    }

}