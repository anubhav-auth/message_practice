package com.anubhav_auth.message_practice.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anubhav_auth.message_practice.utils.NavArguments

@Composable
fun HomeScreen(viewModel: MessageViewModel, navController: NavController) {
    val uniqueSenders by viewModel.uniqueSenders.collectAsState()
    val lastMessageBetweenUsers by viewModel.lastMessageBetweenUsers.collectAsState()
    Scaffold { paddingVal ->
        Column (modifier = Modifier.padding(paddingVal),){
            TopicInputField(text = "loggedinuser") {
                viewModel.loggedInUserId.value = it
                viewModel.subscribeToTopic(it)
                viewModel.subscribeToUpdates(it)
            }
            TopicInputField(text = "chatpartner") {
                viewModel.chatPartnerID.value = it
                viewModel.sendMessage(it)
            }
            Spacer(modifier = Modifier.height(8.dp))
            ChatMenu(

                uniqueSenders = uniqueSenders,
                lastMessageBetweenUsers = lastMessageBetweenUsers,
                navController = navController,
                messageViewModel = viewModel
            )
        }
    }

}

@Composable
fun TopicInputField(modifier: Modifier = Modifier, text:String, onMessageSent: (String) -> Unit) {
    var messageContent by remember { mutableStateOf("") }

    Row(
        modifier = modifier
    ) {
        TextField(
            value = messageContent,
            onValueChange = { messageContent = it },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .border(3.dp, Color.Black, RoundedCornerShape(8.dp)),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(onSend = {
                onMessageSent(messageContent)
            })
        )
        Button(onClick = {
            onMessageSent(messageContent)
        }) {
            Text(text = text)
        }
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
                messageViewModel.chatPartnerID.value = it
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