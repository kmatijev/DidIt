package com.example.didit.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserObject :: class], version = 5)
abstract class UserDatabase : RoomDatabase() {

    companion object {
        const val NAME = "User_DB"
    }

    abstract fun getUserDao(): UserDao
}