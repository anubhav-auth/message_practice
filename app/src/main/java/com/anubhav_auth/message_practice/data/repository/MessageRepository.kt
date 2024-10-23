package com.anubhav_auth.message_practice.data.repository

import android.util.Log
import com.anubhav_auth.message_practice.data.local.MessagesBackLogDAO
import com.anubhav_auth.message_practice.data.local.MessagesDAO
import com.anubhav_auth.message_practice.data.local.MessagesStatusUpdateBackLogDAO
import com.anubhav_auth.message_practice.data.local.TopicsSubscribedDAO
import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.data.model.MessageBackLog
import com.anubhav_auth.message_practice.data.model.MessageStatusUpdates
import com.anubhav_auth.message_practice.data.model.TopicsSubscribed
import com.anubhav_auth.message_practice.data.remote.ApolloMessageClient
import com.anubhav_auth.message_practice.utils.ConnectionState
import com.anubhav_auth.message_practice.utils.toBackLog
import com.anubhav_auth.message_practice.utils.toMessage
import com.anubhav_auth.type.MessageStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

class MessageRepository @Inject constructor(
    private val apolloMessageClient: ApolloMessageClient,
    private val messagesDAO: MessagesDAO,
    private val messagesBackLogDAO: MessagesBackLogDAO,
    private val messageStatusUpdatesDAO: MessagesStatusUpdateBackLogDAO,
    private val topicsSubscribedDAO: TopicsSubscribedDAO
) {

    val connectionState: Flow<ConnectionState> = apolloMessageClient.authState

    suspend fun subscribeToTopic(topic: String): Flow<Message?> {
        return channelFlow {
            apolloMessageClient.subscribeToTopic(topic).collectLatest { message ->
                message?.let {
                    Log.d("ApolloMessageClient1", "subscribeToTopic: $it")
                    messagesDAO.upsertMessage(it)
                    send(it)
                }
            }
        }
    }
    suspend fun subscribeToUpdates(topic: String): Flow<MessageStatusUpdates?> {
        return channelFlow {
            apolloMessageClient.subscribeToMessageStatusUpdates(topic).collectLatest { message ->
                message?.let {
                    Log.d("ApolloMessageClient1", "subscribeToUpdates: $it")
                    messagesDAO.getMessageFromId(it.id).let { msg->
                        val newMsg = msg.copy(
                            status = it.status,
                            deliveredAt = it.deliveredAt,
                            readAt = it.readAt
                        )
                        messagesDAO.upsertMessage(newMsg)
                        send(it)
                    }
                }
            }
        }
    }

    suspend fun sendUpdate(id:String, topic: String, status: MessageStatus): MessageStatusUpdates? {

        val readTime = if(status == MessageStatus.READ) LocalDateTime.now().toString() else ""
        val deliveredTime = if(status == MessageStatus.DELIVERED) LocalDateTime.now().toString() else ""

        val newMsg = MessageStatusUpdates(
            id = id,
            receiver = topic,
            status = status,
            deliveredAt = deliveredTime,
            readAt = readTime
        )

        messageStatusUpdatesDAO.upsertMessageStatus(newMsg)

        val messageUpdateFromServer =
            apolloMessageClient.statusUpdate(newMsg,status)

        messageUpdateFromServer?.let {
            val msg = messagesDAO.getMessageFromId(it.id).copy(
                status = it.status,
                deliveredAt = it.deliveredAt,
                readAt = it.readAt
            )
            messagesDAO.upsertMessage(msg)
            messageStatusUpdatesDAO.deleteMessageStatus(it)
        }
        return messageUpdateFromServer
    }

    suspend fun sendMessage(topic: String, sender: String, message: String): Message? {
        val id = "${LocalDateTime.now()}_${Random.nextInt()}_${UUID.randomUUID()}"
        val newMsg = Message(
            id = id,
            topic = topic,
            content = message,
            sender = sender,
            receiver = topic,
            status = MessageStatus.UNSENT,
            sentAt = LocalDateTime.now().toString(),
            deliveredAt = "",
            readAt = ""
        )
        messagesDAO.upsertMessage(newMsg)
        messagesBackLogDAO.upsertMessage(newMsg.toBackLog())

        val messageFromServer =
            apolloMessageClient.sendMessage(newMsg)
        messageFromServer?.let {
            messagesDAO.upsertMessage(it)
            messagesBackLogDAO.deleteMessage(it.toBackLog())
        }
        return messageFromServer
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

    private fun getAllMessagesBackLog(): Flow<List<MessageBackLog>> {
        return messagesBackLogDAO.getAllMessagesBackLog()
    }
    private fun getAllMessagesStatusUpdateBackLog(): Flow<List<MessageStatusUpdates>> {
        return messageStatusUpdatesDAO.getAllMessagesStatusUpdateBackLog()
    }

    suspend fun clearBackLog(){
        getAllMessagesBackLog().collectLatest { backLogMess->
            backLogMess.forEach {
                apolloMessageClient.sendMessage(it.toMessage())?.let { message->
                    messagesDAO.upsertMessage(message)
                    messagesBackLogDAO.deleteMessage(message.toBackLog())
                }
            }
        }

        getAllMessagesStatusUpdateBackLog().collectLatest { updateBackLog->
            updateBackLog.forEach {
                apolloMessageClient.statusUpdate(it,it.status)?.let {messUp->
                    val msg = messagesDAO.getMessageFromId(messUp.id).copy(
                        status = messUp.status,
                        deliveredAt = messUp.deliveredAt,
                        readAt = messUp.readAt
                    )
                    messagesDAO.upsertMessage(msg)
                    messageStatusUpdatesDAO.deleteMessageStatus(messUp)
                }
            }

        }
    }

}