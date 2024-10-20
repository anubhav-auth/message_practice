package com.anubhav_auth.message_practice.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//import androidx.room.Entity
//import androidx.room.PrimaryKey

@Entity
data class TopicsSubscribed(
    @PrimaryKey
    val topicName: String
)
