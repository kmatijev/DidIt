package com.example.didit.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.didit.viewmodels.AuthState
import com.example.didit.viewmodels.AuthViewModel

@Composable
fun ForgotPasswordPage(
    viewModel: AuthViewModel,
    onPasswordReset: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        viewModel.resetAuthState()  // Reset the auth state
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email input field
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Send Reset Email button
                Button(onClick = {
                    viewModel.resetPassword(email) { _ -> {} }
                }) {
                    Text("Send Reset Email")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Back to Login button
                TextButton(onClick = onBackToLoginClick) {
                    Text("Back to Login")
                }
            }
        }
    )
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                snackbarHostState.showSnackbar(errorMessage)
            }
            is AuthState.PasswordReset -> {
                snackbarHostState.showSnackbar("Password reset email sent.")
                onPasswordReset()
            }
            else -> {}
        }
    }
}
