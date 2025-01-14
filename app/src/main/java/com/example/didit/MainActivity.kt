package com.example.didit

import android.os.Bundle
import android.util.Log
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.didit.pages.FinishedTasksPage
import com.example.didit.pages.ForgotPasswordPage
import com.example.didit.pages.LoginPage
import com.example.didit.pages.ProfilePage
import com.example.didit.pages.RegisterPage
import com.example.didit.pages.TaskStatisticsPage
import com.example.didit.pages.TodoListPage
import com.example.didit.ui.theme.DidItTheme
import com.example.didit.viewmodels.AuthState
import com.example.didit.viewmodels.AuthViewModel
import com.example.didit.viewmodels.StatisticsViewModel
import com.example.didit.viewmodels.TodoViewModel
import com.example.didit.viewmodels.UserViewModel
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var statisticsViewModel: StatisticsViewModel
    private lateinit var userViewModel : UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        var authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Initialize the TodoViewModel with the AuthViewModel
        todoViewModel = TodoViewModel(application, authViewModel)

        // Initialize the StatisticsViewModel with the AuthViewModel
        statisticsViewModel = StatisticsViewModel(application, authViewModel)
        userViewModel = UserViewModel(application, authViewModel)

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
                    val navController = rememberNavController()
                    MainApp(
                        todoViewModel,
                        authViewModel,
                        userViewModel,
                        statisticsViewModel,
                        navController) // MainApp composable that manages navigation
                }
            }
        }
    }
}

@Composable
fun MainApp(
    todoViewModel: TodoViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    statisticsViewModel: StatisticsViewModel,
    navController: NavHostController
) {
    // Observe the authentication state
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // Observe login state and handle navigation
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("todoListPage")
        } else {
            navController.navigate("loginPage")
        }
    }

    NavHost(
        navController = navController,
        startDestination = "loginPage"
    ) {
        // Login Page
        composable("loginPage") {
            LoginPage(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("todoListPage") {
                        // Make sure to pop the login page from the back stack after login
                        popUpTo("loginPage") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate("registerPage")
                },
                onForgotPasswordClick = {
                    navController.navigate("forgotPasswordPage")
                }
            )
        }

        // Register Page
        composable("registerPage") {
            RegisterPage(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    authViewModel.resetAuthState()
                    navController.navigate("loginPage") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    } },
                onBackToLoginClick = {
                    navController.popBackStack() }
            )
        }

        // Forgotten Password Page
        composable("forgotPasswordPage") {
            ForgotPasswordPage(
                viewModel = authViewModel,
                onPasswordReset = {
                    navController.navigate("loginPage") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToLoginClick = {
                    navController.popBackStack()
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
                },
                onStatisticsClick = {
                    navController.navigate("taskStatisticsPage")
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
                },
                onStatisticsClick = {
                    navController.navigate("taskStatisticsPage")
                }
            )
        }

        // Profile Page
        composable("profilePage") {
            ProfilePage(
                viewModel = todoViewModel,
                authViewModel = authViewModel,
                userViewModel = userViewModel,
                onTasksClick = {
                    navController.navigate("todoListPage")
                },
                onFinishedTasksClick = {
                    navController.navigate("finishedTasksPage")
                },
                onStatisticsClick = {
                    navController.navigate("taskStatisticsPage")
                },
                onLogout = {
                    //todoViewModel.clearProfileImage()
                    // Logout and clear the back stack, going back to the login page
                    navController.navigate("loginPage") {
                        Log.d("MainApp", "Logging out and clearing back stack")
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("taskStatisticsPage") {
            TaskStatisticsPage(
                statisticsViewModel = statisticsViewModel,
                authViewModel = authViewModel,
                onTasksClick = {
                    navController.navigate("todoListPage")
                },
                onFinishedTasksClick = {
                    navController.navigate("finishedTasksPage")
                },
                onProfileClick = {
                    navController.navigate("profilePage")
                }
            )
        }
    }
}