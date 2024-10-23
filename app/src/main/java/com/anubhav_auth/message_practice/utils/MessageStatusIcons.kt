package com.anubhav_auth.message_practice.utils

import com.anubhav_auth.message_practice.R
import com.anubhav_auth.type.MessageStatus


class MessageStatusIcons(messageStatus: MessageStatus) {
    val icon = when (messageStatus) {
        MessageStatus.SENT -> R.drawable.check
        MessageStatus.DELIVERED -> R.drawable.read
        MessageStatus.READ -> R.drawable.read
        MessageStatus.UNSENT -> R.drawable.pending
        MessageStatus.UNKNOWN__ -> TODO()
    }

}