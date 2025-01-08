package com.example.didit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfilePage(
    viewModel: TodoViewModel,
    authViewModel: AuthViewModel,
    onTasksClick: () -> Unit,
    onFinishedTasksClick: () -> Unit,
    onLogout: () -> Unit
) {
    val isDarkMode = viewModel.isDarkMode.collectAsState().value

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Profile",
                onProfileClick = {},
                onTasksClick = onTasksClick,
                onFinishedTasksClick = onFinishedTasksClick
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // Theme toggle switch
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Text that changes based on the theme
                    Text(
                        text = if (isDarkMode) "Dark Theme" else "Light Theme",
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    // Switch for toggling theme
                    Switch(
                        checked = isDarkMode,  // Bind the switch state to the ViewModel's isDarkMode state
                        onCheckedChange = { viewModel.toggleTheme() } // Toggle the theme when changed
                    )
                }
                Button(
                    onClick = {
                        authViewModel.logout()
                        onLogout()     // Navigate back to the login screen
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                ) {
                    Text(text = "Logout", color = Color.White)
                }
            }
        }
    )
}