package com.anubhav_auth.message_practice.ui.message

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ChatScreen(navController: NavController, viewModel: MessageViewModel) {
    val chats by viewModel.messagesBetweenUsers.collectAsState()

    BackHandler {
        navController.navigateUp()
    }

    Scaffold { _ ->
        LazyColumn {
            items(chats) { chat ->
                Text(text = chat.content)
            }
            item {
                var messageContent by remember { mutableStateOf("") }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = messageContent,
                        onValueChange = { messageContent = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                viewModel.sendMessage(
                                    messageContent
                                )
                                messageContent = ""
                            }
                        )
                    )
                    Button(onClick = {
                        viewModel.sendMessage(messageContent)
                        messageContent = ""
                    }) {
                        Text(text = "Send")
                    }
                }
            }
        }
    }

}