package com.example.didit.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.didit.MainApplication
import kotlinx.coroutines.launch

class UserViewModel(application: Application, private val authViewModel: AuthViewModel) : AndroidViewModel(application) {

    private val userDao = MainApplication.userDatabase.getUserDao()
    private var isSaveInProgress = false

    private val userId = authViewModel.userId

    // LiveData for profile image URL
    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: LiveData<String?> = _profileImageUrl

    init {
        // Fetch the profile image URL when the ViewModel is initialized (app restart or on app start)
        fetchProfileImageUrl(userId)  // Make sure userId is passed correctly here
    }

    // Function to fetch the profile image URL from the database
    fun fetchProfileImageUrl(currentId: String) {
        viewModelScope.launch {
            try {
                val url = userDao.getProfileImageUrl(currentId)
                _profileImageUrl.postValue((url ?: "").toString())
            } catch (e: Exception) {
                Log.e("TodoViewModel", "Failed to fetch profile image: ${e.message}")
                _profileImageUrl.postValue("") // Fallback to empty string
            }
        }
    }

    fun updateProfileImage(currentId: String, newProfileImageUrl: String) {
        if (isSaveInProgress) return // Ignore subsequent calls

        isSaveInProgress = true
        viewModelScope.launch {
            try {
                userDao.updateProfileImage(currentId, newProfileImageUrl)
                _profileImageUrl.postValue(newProfileImageUrl)
            } finally {
                isSaveInProgress = false
            }
        }
    }

}