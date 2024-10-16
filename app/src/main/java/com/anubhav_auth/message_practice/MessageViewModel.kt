package com.anubhav_auth.message_practice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val apolloClient: ApolloMessageClient
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()


//    init {
//        subscribe("abcd")
//    }

    fun sendMessage(message: String, topic: String) {
        viewModelScope.launch {
            apolloClient.sendMessage("9883192692", topic, message)
        }
    }

    fun subscribe(topic: String) {

        viewModelScope.launch {
            apolloClient.subscribeToTopic(topic)
                .collectLatest { response ->
                    Log.d("mytag", "Received message: ${response.toString()}")
                    if (response != null) {
                        _messages.update {
                            it + response
                        }
                    }
                }
        }
    }
}