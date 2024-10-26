package com.anubhav_auth.message_practice.ui.message

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.anubhav_auth.message_practice.data.model.Message
import com.anubhav_auth.message_practice.utils.MessageStatusIcons
import com.anubhav_auth.message_practice.utils.getFormattedTime
import com.anubhav_auth.type.MessageStatus
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(navController: NavController, viewModel: MessageViewModel) {
    val chats by viewModel.messagesBetweenUsers.collectAsState()
    val chatPartnerID = viewModel.chatPartnerID
    val currentUser = viewModel.loggedInUserId
    BackHandler {
        navController.navigateUp()
        viewModel.cleanMessageBTWUsers()
    }

    Scaffold { padd ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padd)
        ) {
            ChatList(
                modifier = Modifier,
                chats = chats,
                currentUser = currentUser.value,
                updateToRead = { id, sender, status ->
                    viewModel.sendUpdate(id, sender, status)
                }
            )
            ChatInputField(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
                onMessageSent = { message ->
                    viewModel.sendMessage(message)
                })
        }

    }

}

@Composable
fun ChatList(
    modifier: Modifier = Modifier,
    chats: List<Message>,
    currentUser: String,
    updateToRead: (id: String, sender: String, status: MessageStatus) -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val firstUnreadIndex = chats.indexOfFirst { chat -> chat.status == MessageStatus.DELIVERED }
    val unreadMessagesCount = chats.count { chat -> chat.status == MessageStatus.DELIVERED }
    val scrollToIndex = if (firstUnreadIndex == -1) chats.size - 1 else firstUnreadIndex

    LazyColumn(modifier = modifier, state = listState) {
        items(chats) { chat ->
            ChatItem(chat = chat, currentUser = currentUser)
        }
    }
    LaunchedEffect(listState) {
        scope.launch {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                .map { info ->
                    info.mapNotNull { visibleItemInfo ->
                        chats.getOrNull(visibleItemInfo.index)
                    }
                }
                .distinctUntilChanged()
                .collect { visibleItems ->
                    visibleItems.forEach {
                        if (it.sender != currentUser && it.status != MessageStatus.READ) {
                            updateToRead(it.id, it.sender, MessageStatus.READ)
                        }
                    }
                }
        }
    }

    LaunchedEffect(chats) {
        if (chats.isNotEmpty()) {
            listState.scrollToItem(scrollToIndex)

            listState.layoutInfo.visibleItemsInfo.mapNotNull { visibleItem ->
                chats.getOrNull(visibleItem.index)
            }.forEach { message ->
                if (message.sender != currentUser && message.status != MessageStatus.READ) {
                    Log.d("mytag", " chat list: Updating message with ID: ${message.id}")
                    updateToRead(message.id, message.sender, MessageStatus.READ)
                }
            }

        }
    }
}

@Composable
private fun ChatItem(modifier: Modifier = Modifier, chat: Message, currentUser: String) {
    val isMessageSentByCurrentUser = chat.sender == currentUser
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Green)
                .fillMaxWidth(0.45f)
                .padding(8.dp)
                .align(
                    if (isMessageSentByCurrentUser) Alignment.CenterEnd
                    else Alignment.CenterStart
                )

        ) {
            Text(
                text = chat.content, textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = chat.sentAt.getFormattedTime(),
                    textAlign = TextAlign.End
                )
                Icon(
                    painter = painterResource(id = MessageStatusIcons(chat.status).icon),
                    contentDescription = "",
                    tint = if (chat.status == MessageStatus.READ) Color.Blue else Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

    }
}

@Composable
fun ChatInputField(modifier: Modifier = Modifier, onMessageSent: (String) -> Unit) {
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
                messageContent = ""
            })
        )
        Button(onClick = {
            onMessageSent(messageContent)
            messageContent = ""
        }) {
            Text(text = "Send")
        }
    }
}

