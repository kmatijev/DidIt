package com.example.didit

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.didit.db.UserObject

@Composable
fun ProfilePage(
    viewModel: TodoViewModel,
    authViewModel: AuthViewModel,
    onTasksClick: () -> Unit,
    onFinishedTasksClick: () -> Unit,
    onLogout: () -> Unit
) {
    val isDarkMode = viewModel.isDarkMode.collectAsState().value

    val user by viewModel.user.observeAsState()

    // The username state that is bound to the TextField
    var username by remember { mutableStateOf("") }

    // Fetch user data when the screen is first loaded or userId changes
    LaunchedEffect(authViewModel.userId) {
        // Fetch user info based on the userId
        viewModel.fetchUser(authViewModel.userId)
    }

    // Update the username state whenever the user data is updated
    LaunchedEffect(user) {
        user?.let {
            username = it.username  // Sync the username with the user data
        }
    }

    Log.d("ProfilePage", "Username: $username")

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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Username Field
                        Text(text = "Username:")

                        TextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save Button
                        Button(onClick = {
                            viewModel.updateUser(username, user!!.email, authViewModel.userId)
                        }) {
                            Text("Save Changes")
                        }
                    }
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