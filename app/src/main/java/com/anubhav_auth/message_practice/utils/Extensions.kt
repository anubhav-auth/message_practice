package com.anubhav_auth.message_practice.utils

import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.data.model.MessageBackLog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun List<String>.checkUsers(currentUser: String): List<String> {
    return this.filter { it != currentUser }
}

fun String.getFormattedTime(): String {
    val localDateTime = LocalDateTime.parse(this)
    val newFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a")

    // Format the LocalDateTime to a new String format
    val formattedDateTimeString = localDateTime.format(newFormatter)
    return formattedDateTimeString
}

fun Message.toBackLog():MessageBackLog{
    return MessageBackLog(
        id = this.id,
        topic = this.topic,
        content = this.content,
        sender = this.sender,
        receiver = this.receiver,
        status = this.status,
        sentAt = this.sentAt,
        deliveredAt = this.deliveredAt,
        readAt = this.readAt
    )
}

fun MessageBackLog.toMessage():Message{
    return Message(
        id = this.id,
        topic = this.topic,
        content = this.content,
        sender = this.sender,
        receiver = this.receiver,
        status = this.status,
        sentAt = this.sentAt,
        deliveredAt = this.deliveredAt,
        readAt = this.readAt
    )
}