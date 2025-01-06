package com.example.didit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.didit.ui.theme.DidItTheme
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Initialize the database in MainActivity (if needed for the app's setup)
        val todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]


        enableEdgeToEdge() // Your setup code for UI if necessary

        // Set content with navigation
        setContent {
            val isDarkMode by todoViewModel.isDarkMode.collectAsState()
            // Use the isDarkMode state to determine the theme
            DidItTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(todoViewModel, authViewModel) // MainApp composable that manages navigation
                }
            }
        }
    }
}

@Composable
fun MainApp(
    todoViewModel: TodoViewModel,
    authViewModel: AuthViewModel // Pass AuthViewModel for login management
) {
    val navController = rememberNavController() // Create a NavController

    // Observe the authentication state
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // Observe login state and handle navigation
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("todoListPage") {
                popUpTo(0) { inclusive = true } // Clear back stack to avoid login screen navigation issues
            }
        } else {
            navController.navigate("loginPage") {
                popUpTo(0) { inclusive = true } // Clear back stack for logout
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "todoListPage" else "loginPage"
    ) {
        // Login Page
        composable("loginPage") {
            LoginPage(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("todoListPage")
                }
            )
        }

        // To-do List Page
        composable("todoListPage") {
            TodoListPage(
                viewModel = todoViewModel,
                onFinishedTasksClick = {
                    navController.navigate("finishedTasksPage")
                },
                onProfileClick = {
                    navController.navigate("profilePage")
                }
            )
        }

        // Finished Tasks Page
        composable("finishedTasksPage") {
            FinishedTasksPage(
                viewModel = todoViewModel,
                onTasksClick = {
                    navController.navigate("todoListPage")
                },
                onProfileClick = {
                    navController.navigate("profilePage")
                }
            )
        }

        // Profile Page
        composable("profilePage") {
            ProfilePage(
                viewModel = todoViewModel,
                onTasksClick = {
                    navController.navigate("todoListPage")
                },
                onFinishedTasksClick = {
                    navController.navigate("finishedTasksPage")
                }
            )
        }
    }
}