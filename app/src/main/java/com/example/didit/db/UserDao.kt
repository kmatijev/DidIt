package com.example.didit.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): UserObject

    @Update
    suspend fun updateUser(user: UserObject)

    @Insert
    suspend fun addUser(user: UserObject)

    @Query("SELECT profileImageUrl FROM users WHERE userId = :userId")
    fun getProfileImageUrl(userId: String): LiveData<String?>

    @Query("UPDATE users SET profileImageUrl = :url WHERE userId = :userId")
    suspend fun updateProfileImage(userId: String, url: String)

}