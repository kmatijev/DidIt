package com.example.didit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.didit.db.UserDao
import kotlinx.coroutines.launch

class UserViewModel(application: Application, private val authViewModel: AuthViewModel) : AndroidViewModel(application) {

    private val userDao = MainApplication.userDatabase.getUserDao()

    // LiveData for profile image URL
    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: LiveData<String?> = _profileImageUrl

    init {
        // Fetch the profile image URL when the ViewModel is initialized (app restart or on app start)
        fetchProfileImageUrl(authViewModel.userId)  // Make sure userId is passed correctly here
    }

    // Function to fetch the profile image URL from the database
    fun fetchProfileImageUrl(userId: String) {
        userDao.getProfileImageUrl(userId).observeForever {
            _profileImageUrl.postValue(it)
        }
    }

    // Function to update the profile image URL
    fun updateProfileImage(userId: String, url: String) {
        viewModelScope.launch {
            userDao.updateProfileImage(userId, url)
            fetchProfileImageUrl(userId)
        }
    }
}