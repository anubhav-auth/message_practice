package com.anubhav_auth.message_practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

@Composable
fun MessageScreen(viewModel: MessageViewModel) {
    // State to hold the message to be sent
    var messageContent by remember { mutableStateOf("") }

    // Collect messages from ViewModel
    val messages by viewModel.messages.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(25.dp))
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
                            messageContent,
                            "abcd"
                        ) // Send message on pressing send
                        messageContent = "" // Clear input field
                    }
                )
            )
            Button(onClick = {
                viewModel.sendMessage(messageContent, "abcd") // Send message on button click
                messageContent = "" // Clear input field
            }) {
                Text(text = "Send")
            }
            Button(
                onClick = {
                    viewModel.subscribe("abcd")
                },
                enabled = true
            ) {
                Text(text = "Sub")
            }
        }
        // Message List
        LazyColumn(
            reverseLayout = true, // Show latest message on top
            modifier = Modifier.weight(1f)
        ) {
            items(messages) { message ->
                MessageItem(message = message)
            }
        }

        // Message Input Field and Send Button

    }
}

@Composable
fun MessageItem(message: Message) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = "${message.sender}: ${message.content}",
            modifier = Modifier.padding(8.dp)
        )
    }
}
