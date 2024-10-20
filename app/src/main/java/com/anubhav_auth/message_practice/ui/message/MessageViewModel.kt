package com.anubhav_auth.message_practice.ui.message

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messagesRepository: MessageRepository
) : ViewModel() {

    private val loggedInUserId = "+919883192692"
    var chatPartnerID = null

    private val _uniqueSenders = MutableStateFlow<List<String>>(emptyList())
    val uniqueSenders = _uniqueSenders.asStateFlow()

    private val _messagesBetweenUsers = MutableStateFlow<List<Message>>(emptyList())
    val messagesBetweenUsers = _messagesBetweenUsers.asStateFlow()

    private val _lastMessageBetweenUsers = MutableStateFlow<Map<String, String>>(emptyMap())
    val lastMessageBetweenUsers = _lastMessageBetweenUsers.asStateFlow()


    init {
//            viewModelScope.launch {
//                messagesRepository.upsertTopic("abcd")
//                messagesRepository.upsertTopic("abcde")
//                messagesRepository.upsertTopic("abcdf")
//            }
            getAllUniqueSenders()
            subscribeToTopic("abcd")
//            subscribeToAllTopics()

    }

    fun subscribeToTopic(topic: String) {
        viewModelScope.launch {
            messagesRepository.subscribeToTopic(topic).collectLatest {
                if (it != null) {
                    Log.d("ApolloMessageClient", "subscribeToTopic: $it")
                    getLastMessageBetweenUsers(it.sender)
                    getAllUniqueSenders()
                    if (it.sender == chatPartnerID) {
                        _messagesBetweenUsers.update { newMessages ->
                            newMessages.toMutableList().apply {
                                add(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun subscribeToAllTopics() {
        viewModelScope.launch {
            messagesRepository.getAllTopics().forEach { topic ->
                messagesRepository.subscribeToTopic(topic.topicName).collectLatest {
                    if (it != null) {
                        getLastMessageBetweenUsers(it.sender)
                        getAllUniqueSenders()
                        if (it.sender == chatPartnerID) {
                            _messagesBetweenUsers.update { newMessages ->
                                newMessages.toMutableList().apply {
                                    add(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getAllUniqueSenders() {
        viewModelScope.launch {
            Log.d("ApolloMessageClient", "getAllUniqueSenders: ")
            _uniqueSenders.update {
                messagesRepository.getAllUniqueSenders()
            }
        }
    }

    fun getMessageBetweenUsers(chatPartnerID: String) {
        viewModelScope.launch {
            messagesRepository.getMessageBetweenUsers(loggedInUserId, chatPartnerID).collectLatest {
                _messagesBetweenUsers.value = it
            }
        }
    }

    fun getLastMessageBetweenUsers(chatPartnerID: String) {
        viewModelScope.launch {
            messagesRepository.getLastMessageBetweenUsers(loggedInUserId, chatPartnerID)
                .collectLatest { message ->
                    message?.let {
                        _lastMessageBetweenUsers.update { currentmsgs ->
                            currentmsgs.toMutableMap().apply {
                                put(chatPartnerID, it.content)
                            }
                        }
                    }
                }
        }

        fun sendMessage(topic: String, sender: String, message: String) {
            viewModelScope.launch {
                messagesRepository.sendMessage(topic, sender, message)
            }
        }

        fun deleteMessage(message: Message) {
            viewModelScope.launch {
                messagesRepository.deleteMessage(message)
            }
        }

        fun upsertTopic(topic: String) {
            viewModelScope.launch {
                messagesRepository.upsertTopic(topic)
            }
        }

        fun deleteTopic(topic: String) {
            viewModelScope.launch {
                messagesRepository.deleteTopic(topic)
            }
        }

    }
}