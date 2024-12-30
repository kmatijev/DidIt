package com.example.didit

import android.app.Application
import androidx.room.Room
import com.example.didit.db.TodoDatabase
import com.example.didit.utils.NotificationUtil

class MainApplication : Application() {

    companion object {
        lateinit var todoDatabase : TodoDatabase
    }
        override fun onCreate() {
            super.onCreate()
            NotificationUtil.createNotificationChannel(this)
            todoDatabase = Room.databaseBuilder(
                applicationContext,
                TodoDatabase::class.java,
                TodoDatabase.NAME
            )
                .fallbackToDestructiveMigration() // Remove after table is DONE
                .build()
    }
}