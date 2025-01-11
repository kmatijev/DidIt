package com.example.didit.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserObject(
    @PrimaryKey val userId: String,
    val username: String,
    val email: String,
    val profileImageUrl: String? = null
)