package com.anubhav_auth.message_practice.ui.message

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun HomeScreen(viewModel: MessageViewModel, navController: NavController) {
    val uniqueSenders by viewModel.uniqueSenders.collectAsState()
    val lastMessageBetweenUsers by viewModel.lastMessageBetweenUsers.collectAsState()
    Log.d("ApolloMessageClient", "unique: ${uniqueSenders}")

    ChatMenu(uniqueSenders = uniqueSenders, lastMessageBetweenUsers = lastMessageBetweenUsers)

}

@Composable
fun test(modifier: Modifier = Modifier) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Red))
}

@Composable
fun ChatMenu(
    uniqueSenders: List<String>,
    lastMessageBetweenUsers: Map<String, String>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(uniqueSenders){ uniqueSender->
            lastMessageBetweenUsers[uniqueSender]?.let {
                ChatItemContent(
                    sender = uniqueSender,
                    lastMessage = it
                )
            }?.let {
                ChatItem(chatItemContent = it) {
                    //TODO
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    chatItemContent: ChatItemContent,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.Cyan)
            .clickable {
                onClick()
            }
    ) {
        Text(text = chatItemContent.sender)
        Text(text = chatItemContent.lastMessage)
    }
}

data class ChatItemContent(
    val sender: String,
    val lastMessage: String
)