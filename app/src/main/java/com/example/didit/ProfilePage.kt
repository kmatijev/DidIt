package com.example.didit

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ProfilePage(
    viewModel: TodoViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onTasksClick: () -> Unit,
    onFinishedTasksClick: () -> Unit,
    onStatisticsClick: () -> Unit,
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

    // Observe the profile image URL from ViewModel
    val profileImageUrl by userViewModel.profileImageUrl.observeAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // Update profile image in database
            userViewModel.updateProfileImage(authViewModel.userId, uri.toString())
        }
    }

    LaunchedEffect(authViewModel.userId) {
        userViewModel.fetchProfileImageUrl(authViewModel.userId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Profile",
                onProfileClick = {},
                onStatisticsClick = onStatisticsClick,
                onTasksClick = onTasksClick,
                onFinishedTasksClick = onFinishedTasksClick
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    // Check if profile image URL is not null or empty
                    if (profileImageUrl.isNullOrEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_person_24), // Default icon
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            tint = Color.White
                        )
                    } else {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button to select a new profile picture
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Change Profile Picture")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Username Label
                Text(
                    text = "Username:",
                    modifier = Modifier.align(Alignment.Start) // Align text to the start of the column
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Username TextField
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save Changes Button
                Button(
                    onClick = {
                        viewModel.updateUser(username, user?.email ?: "", authViewModel.userId, userViewModel.profileImageUrl.value ?: "")
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    enabled = username.isNotBlank() // Enable only when username is not blank
                ) {
                    Text("Save Changes")
                }

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
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Logout")
                    }

            }

        }
    )
}