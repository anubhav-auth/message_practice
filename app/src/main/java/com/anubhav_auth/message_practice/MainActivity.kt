package com.anubhav_auth.message_practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anubhav_auth.message_practice.ui.message.HomeScreen
import com.anubhav_auth.message_practice.ui.message.MessageScreen
import com.anubhav_auth.message_practice.ui.message.MessageViewModel
import com.anubhav_auth.message_practice.ui.theme.Message_practiceTheme
import com.anubhav_auth.message_practice.utils.NavArguments
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = hiltViewModel<MessageViewModel>()
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = NavArguments.HOMESCREEN.toString()){
                composable(NavArguments.HOMESCREEN.toString()){
                    HomeScreen(viewModel = viewModel, navController = navController)
                }
                composable(NavArguments.CHATSCREEN.toString()){
                }
            }
        }
    }
}