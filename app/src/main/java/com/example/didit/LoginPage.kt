package com.example.didit


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginPage(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.resetAuthState()  // Reset the auth state
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            // Your login form UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    enabled = email.isNotBlank() && password.isNotBlank(),
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Login")
                }
                Button(
                    onClick = { onRegisterClick() },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Register")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onForgotPasswordClick) {
                    Text("Forgot Password?")
                }
            }

            if (authState is AuthState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        //.background(Color.Black.copy(alpha = 0.5f)) // Optional dim background
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    )

    // Show error message as a Snackbar
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            val errorMessage = (authState as AuthState.Error).message
            snackbarHostState.showSnackbar(errorMessage)
        } else if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }
}