package com.example.didit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

    // Track whether the user is logged in
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // Firebase user data
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user

    // Authentication state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Login function - called from the UI
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = authenticateUser(email, password)
                _user.value = result
                _authState.value = AuthState.Success
                _isLoggedIn.value = true  // User is logged in
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
                _isLoggedIn.value = false  // Authentication failed
            }
        }
    }

    // Authenticate using FirebaseAuth (suspend function)
    private suspend fun authenticateUser(email: String, password: String): FirebaseUser? {
        return withContext(Dispatchers.IO) {
            try {
                // Use FirebaseAuth to authenticate
                val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
                authResult.user
            } catch (e: Exception) {
                throw e // Rethrow to be caught by the calling function
            }
        }
    }
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
}