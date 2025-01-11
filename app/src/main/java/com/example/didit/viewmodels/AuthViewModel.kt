package com.example.didit.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.didit.MainApplication
import com.example.didit.db.UserObject

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userDao = MainApplication.userDatabase.getUserDao()

    var userId = ""

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
                userId = result?.uid ?: ""
                _authState.value = AuthState.Success
                _isLoggedIn.value = true  // User is logged in
                Log.d("AuthViewModel", "Logged in user: ${result?.uid}")
            } catch (e: Exception) {
                // Customize error messages
                val errorMessage = when {
                    e.message?.contains("email") == true -> "E-mail invalid."
                    e.message?.contains("password") == true -> "Incorrect password."
                    e.message?.contains("network") == true -> "Network error. Please try again."
                    else -> "Login failed. Please check your credentials and try again."
                }
                _authState.value = AuthState.Error(errorMessage)
                _isLoggedIn.value = false  // Authentication failed
            }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        _authState.value = AuthState.LoggedOut
        _user.value = null
        userId = ""
        _isLoggedIn.value = false // Update the state

    }
    // Authenticate using FirebaseAuth (suspend function)
    private suspend fun authenticateUser(email: String, password: String): FirebaseUser? {
        return withContext(Dispatchers.IO) {
            try {
                // Use FirebaseAuth to authenticate
                val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
                authResult.user
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed", e)
                throw e // Rethrow to be caught by the calling function
            }
        }
    }

    fun register(email: String, username: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val newUser = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

                val newUserObject = UserObject(newUser.user?.uid ?: "", username, email)
                userDao.addUser(newUserObject)
                _authState.value = AuthState.Success
                callback(true)
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("email") == true -> "E-mail invalid."
                    e.message?.contains("password") == true -> "Invalid password. Must be between 6 and 30 characters."
                    e.message?.contains("network") == true -> "Network error. Please try again."
                    else -> "Registration failed. Please check your credentials and try again."
                }
                _authState.value = AuthState.Error(errorMessage)
                callback(false)
            }
        }
    }

    fun resetPassword(email: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Send reset password email
                firebaseAuth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordReset
                callback(true)

            } catch (e: Exception) {
                // General error handling
                _authState.value =
                    AuthState.Error("Password reset failed. Please check your e-mail and try again.")
                callback(false)
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle  // You can use a neutral state like Idle
    }
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data object PasswordReset : AuthState()
    data object LoggedOut : AuthState()
    data class Error(val message: String) : AuthState()
}