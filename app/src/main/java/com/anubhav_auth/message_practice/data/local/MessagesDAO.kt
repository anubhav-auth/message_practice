package com.anubhav_auth.message_practice.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.data.model.TopicsSubscribed
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDAO {

    @Upsert
    suspend fun upsertMessage(message: Message)

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("SELECT * FROM MESSAGE WHERE (sender = :loggedInUserId AND receiver = :chatPartnerID) OR (sender = :chatPartnerID AND receiver = :loggedInUserId) ORDER BY sentAt ASC")
    fun getAllMessagesBetweenUsers(loggedInUserId: String, chatPartnerID: String): Flow<List<Message>>

    @Query("SELECT DISTINCT sender FROM message")
    suspend fun getAllUniqueSenders(): List<String>

    @Query("SELECT * FROM MESSAGE WHERE (sender = :loggedInUserId AND receiver = :chatPartnerID) OR (sender = :chatPartnerID AND receiver = :loggedInUserId) ORDER BY sentAt DESC LIMIT 1")
    fun getLastMessageBetweenUsers(loggedInUserId: String, chatPartnerID: String): Flow<Message?>
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