package com.anubhav_auth.message_practice.ui.message

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.data.repository.MessageRepository
import com.anubhav_auth.message_practice.utils.ConnectionState
import com.anubhav_auth.message_practice.utils.checkUsers
import com.anubhav_auth.type.MessageStatus
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

    val connectionState = messagesRepository.connectionState

    val loggedInUserId = "+919883192692"
    var chatPartnerID = ""

    private val _uniqueSenders = MutableStateFlow<List<String>>(emptyList())
    val uniqueSenders = _uniqueSenders.asStateFlow()

    private val _messagesBetweenUsers = MutableStateFlow<List<Message>>(emptyList())
    val messagesBetweenUsers = _messagesBetweenUsers.asStateFlow()

    private val _lastMessageBetweenUsers = MutableStateFlow<Map<String, String>>(emptyMap())
    val lastMessageBetweenUsers = _lastMessageBetweenUsers.asStateFlow()


    init {
        observeConnectionState()
        getAllUniqueSenders()
        subscribeToTopic(loggedInUserId)
        subscribeToUpdates(loggedInUserId)
    }

    private fun observeConnectionState(){
        viewModelScope.launch {
            connectionState.collectLatest { state->
                when(state){
                    ConnectionState.Connected -> {
                        Log.d("ApolloMessageClient", "Connected")
                        messagesRepository.clearBackLog()
                    }
                    is ConnectionState.Disconnected -> {
                        Log.d("ApolloMessageClient", "Disconnected: ${state.reason}")
                    }
                    is ConnectionState.Error -> {
                        Log.d("ApolloMessageClient", "Error: ${state.error}")
                    }
                }
            }
        }
    }

    private fun subscribeToTopic(topic: String) {
        viewModelScope.launch {
            messagesRepository.subscribeToTopic(topic).collectLatest {
                if (it != null) {
                    getLastMessageBetweenUsers("abcd")
                    getAllUniqueSenders()
                    sendUpdate(it.id, it.topic, MessageStatus.DELIVERED)
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

    private fun subscribeToUpdates(topic: String) {
        viewModelScope.launch {
            messagesRepository.subscribeToUpdates(topic).collectLatest {
                Log.d("ApolloMessageClient", "Received message status updates: $it")
            }
        }
    }

    private fun sendUpdate(id:String, topic: String, status: MessageStatus) {
        viewModelScope.launch {
            messagesRepository.sendUpdate(id, topic, status)
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
            val senders = messagesRepository.getAllUniqueSenders().checkUsers(loggedInUserId)
            senders.forEach { sender ->
                getLastMessageBetweenUsers(sender)
            }
            _uniqueSenders.update {
                senders
            }
        }
    }

    fun getMessageBetweenUsers() {
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
                    Log.d("ApolloMessageClient", " last mess${message?.content.toString()}")
                    message?.let {
                        _lastMessageBetweenUsers.update { currentmsgs ->
                            currentmsgs.toMutableMap().apply {
                                put(chatPartnerID, it.content)
                            }
                        }
                    }
                }
        }
    }
    fun sendMessage(message: String) {
        viewModelScope.launch {
            messagesRepository.sendMessage(chatPartnerID, loggedInUserId, message)
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