package com.example.didit.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.didit.Todo

@Database(entities = [Todo::class], version = 2)
@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase(){

    companion object {
        const val NAME = "Todo_DB"
    }

    abstract fun getTodoDao() : TodoDao
}