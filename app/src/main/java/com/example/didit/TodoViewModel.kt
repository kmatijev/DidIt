package com.example.didit

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.didit.utils.NotificationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val todoDao = MainApplication.todoDatabase.getTodoDao()
    private val context = application.applicationContext

    val todoList: LiveData<List<Todo>> = todoDao.getAllTodo()

    fun addTodo(title: String, reminder: Long?, priority: Priority) {
        viewModelScope.launch(Dispatchers.IO) {
            // Create the new task object
            val newTodo = Todo(
                title = title,
                createdAt = Date.from(Instant.now()),
                reminderDate = reminder, // Pass the reminder date
                isChecked = false // Set the initial state of the checkbox to unchecked
                //priority = priority
            )

            // Add the task to the database
            todoDao.addTodo(newTodo)

            // Schedule a notification if a reminder is set
            if (reminder != null) {
                scheduleNotification(title, reminder)
            }
        }
    }

    fun toggleTaskChecked(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateTodo(todo.copy(isChecked = todo.isChecked)) // Update the 'isChecked' field
        }
    }

    fun deleteTodo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteTodo(id)
        }
    }

    private fun scheduleNotification(title: String, reminderTime: Long?) {
        reminderTime?.let {
            val delay = reminderTime - System.currentTimeMillis()

            // Check if the reminder time is in the future
            if (delay > 0) {
                Log.d("TodoViewModel", "Scheduling notification for task: $title in $delay ms")
                // Schedule the notification after the delay
                Handler(Looper.getMainLooper()).postDelayed({
                    NotificationUtil.showNotification(
                        context, // Pass the context here
                        "Task Reminder",
                        "Reminder for task: $title"
                    )
                }, delay)
            }
        }
}
}
