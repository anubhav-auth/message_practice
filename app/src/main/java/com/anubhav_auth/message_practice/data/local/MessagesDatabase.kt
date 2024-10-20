package com.anubhav_auth.message_practice.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.data.model.TopicsSubscribed


@Database(entities = [Message::class, TopicsSubscribed::class], version = 1)
abstract class MessagesDatabase: RoomDatabase() {
    abstract fun messagesDao(): MessagesDAO
    abstract fun topicsSubscribedDao(): TopicsSubscribedDAO
}