package com.example.didit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.didit.ui.theme.DidItTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the database in MainActivity (if needed for the app's setup)
        val todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]

        enableEdgeToEdge() // Your setup code for UI if necessary

        // Set content with navigation
        setContent {
            DidItTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(todoViewModel) // MainApp composable that manages navigation
                }
            }
        }
    }
}

@Composable
fun MainApp(viewModel: TodoViewModel) {
    val navController = rememberNavController() // Create a NavController

    NavHost(
        navController = navController,
        startDestination = "todoListPage"
    ) {
        composable("todoListPage") {
            TodoListPage(
                viewModel = viewModel,
                onTasksClick = {
                    navController.navigate("todoListPage")
                },
                onFinishedTasksClick = {
                    navController.navigate("finishedTasksPage")
                }
            )
        }
        composable("finishedTasksPage") {
            FinishedTasksPage(
                viewModel = viewModel,
                onTasksClick = {
                    navController.navigate("todoListPage")
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}