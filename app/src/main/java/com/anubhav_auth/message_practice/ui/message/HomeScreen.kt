package com.anubhav_auth.message_practice.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.anubhav_auth.message_practice.utils.NavArguments

@Composable
fun HomeScreen(viewModel: MessageViewModel, navController: NavController) {
    val uniqueSenders by viewModel.uniqueSenders.collectAsState()
    val lastMessageBetweenUsers by viewModel.lastMessageBetweenUsers.collectAsState()
    Scaffold { paddingVal ->
        ChatMenu(
            modifier = Modifier.padding(paddingVal),
            uniqueSenders = uniqueSenders,
            lastMessageBetweenUsers = lastMessageBetweenUsers,
            navController = navController,
            messageViewModel = viewModel
        )
    }

}

@Composable
fun ChatMenu(
    modifier: Modifier = Modifier,
    uniqueSenders: List<String>,
    lastMessageBetweenUsers: Map<String, String>,
    navController: NavController,
    messageViewModel: MessageViewModel
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(uniqueSenders) { uniqueSender ->
            ChatItem(
                chatItemContent = ChatItemContent(
                    sender = uniqueSender,
                    lastMessage = lastMessageBetweenUsers[uniqueSender] ?: "No Messages Yet"
                )
            ) {
                messageViewModel.chatPartnerID = it
                messageViewModel.getMessageBetweenUsers()
                navController.navigate(NavArguments.CHATSCREEN.toString())
            }
        }
    }
}

@Composable
fun ChatItem(
    chatItemContent: ChatItemContent,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.Cyan)
            .clickable {
                onClick(chatItemContent.sender)
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