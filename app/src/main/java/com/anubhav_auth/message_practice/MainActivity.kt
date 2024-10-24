package com.anubhav_auth.message_practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anubhav_auth.message_practice.ui.message.ChatScreen
import com.anubhav_auth.message_practice.ui.message.HomeScreen
import com.anubhav_auth.message_practice.ui.message.MessageViewModel
import com.anubhav_auth.message_practice.utils.NavArguments
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val messagesViewModel = hiltViewModel<MessageViewModel>()
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = NavArguments.HOMESCREEN.toString()
            ) {
                composable(NavArguments.HOMESCREEN.toString()) {
                    HomeScreen(viewModel = messagesViewModel, navController = navController)
                }
                composable(NavArguments.CHATSCREEN.toString()) {
                    ChatScreen(viewModel = messagesViewModel, navController = navController)
                }
            }
        }
    }
}