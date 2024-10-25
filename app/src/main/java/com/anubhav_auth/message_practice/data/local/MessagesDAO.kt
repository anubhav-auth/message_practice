package com.anubhav_auth.message_practice.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.data.model.MessageBackLog
import com.anubhav_auth.message_practice.data.model.MessageStatusUpdates
import com.anubhav_auth.message_practice.data.model.TopicsSubscribed
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDAO {

    @Upsert
    suspend fun upsertMessage(message: Message)

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("SELECT * FROM MESSAGE WHERE id = :id")
    suspend fun getMessageFromId(id: String): Message

    @Query("SELECT * FROM MESSAGE WHERE (sender = :loggedInUserId AND receiver = :chatPartnerID) OR (sender = :chatPartnerID AND receiver = :loggedInUserId) ORDER BY sentAt ASC")
    fun getAllMessagesBetweenUsers(
        loggedInUserId: String,
        chatPartnerID: String
    ): Flow<List<Message>>

    @Query("""
    SELECT DISTINCT CASE WHEN sender = :loggedInUserId THEN receiver ELSE sender END AS user
    FROM Message m
    WHERE (sender = :loggedInUserId OR receiver = :loggedInUserId)
    GROUP BY user
    ORDER BY MAX(CASE WHEN sender = :loggedInUserId THEN m.sentAt ELSE m.deliveredAt END) DESC
""")
    suspend fun getAllUniqueSenders(loggedInUserId: String): List<String>

    @Query("SELECT * FROM MESSAGE WHERE (sender = :loggedInUserId AND receiver = :chatPartnerID) OR (sender = :chatPartnerID AND receiver = :loggedInUserId) ORDER BY sentAt DESC LIMIT 1")
    fun getLastMessageBetweenUsers(loggedInUserId: String, chatPartnerID: String): Flow<Message?>
}

@Dao
interface MessagesBackLogDAO {

    @Upsert
    suspend fun upsertMessage(message: MessageBackLog)

    @Delete
    suspend fun deleteMessage(message: MessageBackLog)

    @Query("SELECT * FROM MessageBackLog")
    fun getAllMessagesBackLog(): Flow<List<MessageBackLog>>

}

@Dao
interface MessagesStatusUpdateBackLogDAO {

    @Upsert
    suspend fun upsertMessageStatus(message: MessageStatusUpdates)

    @Delete
    suspend fun deleteMessageStatus(message: MessageStatusUpdates)

    @Query("SELECT * FROM MessageStatusUpdates")
    fun getAllMessagesStatusUpdateBackLog(): Flow<List<MessageStatusUpdates>>

}

@Dao
interface TopicsSubscribedDAO {

    @Upsert
    suspend fun upsertTopic(topic: TopicsSubscribed)

    @Delete
    suspend fun deleteTopic(topic: TopicsSubscribed)

    @Query("SELECT * FROM TopicsSubscribed")
    suspend fun getAllTopics(): List<TopicsSubscribed>
}