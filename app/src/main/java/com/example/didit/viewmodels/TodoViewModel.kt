package com.example.didit.viewmodels

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.didit.db.Category
import com.example.didit.MainApplication
import com.example.didit.db.Priority
import com.example.didit.db.Todo
import com.example.didit.db.UserObject
import com.example.didit.utils.NotificationUtil
import com.example.didit.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Calendar
import java.util.Date

class TodoViewModel(application: Application, private val authViewModel: AuthViewModel) : AndroidViewModel(application) {
    private val todoDao = MainApplication.todoDatabase.getTodoDao()
    private val userDao = MainApplication.userDatabase.getUserDao()

    private val context = application.applicationContext


    private val _user = MediatorLiveData<UserObject>()
    val user: LiveData<UserObject> get() = _user

    private val userId: String
        get() = authViewModel.userId

    private val preferencesManager = PreferencesManager(application)

    // MutableStateFlow to manage the current theme state (light or dark)
    private val _isDarkMode = MutableStateFlow(preferencesManager.getThemePreference())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _todoList = MediatorLiveData<List<Todo>>()
    var allTodos: LiveData<List<Todo>> = _todoList

    val activeTasks: LiveData<List<Todo>> = allTodos.map { list ->
        list.filter { !it.isFinished }
    }.distinctUntilChanged()

    val finishedTasks: LiveData<List<Todo>> = allTodos.map { list ->
        list.filter { it.isFinished }
    }.distinctUntilChanged()

    init {

        _user.addSource(authViewModel.user.asLiveData()) { user ->
            user?.let {
                fetchUser(it.uid)
            }
        }

        _todoList.addSource(authViewModel.user.asLiveData()) { user ->
            user?.let {
                fetchTodosForUser(it.uid)
            }
        }
    }

    fun fetchTodosForUser(userId: String) {
        val userTodos = todoDao.getAllTodos(userId)
        Log.d("TodoViewModel", "Fetching todos for user: $userId")
        _todoList.addSource(userTodos) { todos ->
            _todoList.value = todos
        }
    }

    fun fetchUser(userId: String) {
        viewModelScope.launch {
            val user = userDao.getUser(userId) // Get the user from the database
            _user.value = user // Set the username in state
        }
    }

    fun addTodo(
        title: String,
        reminder: Long?,
        priority: Priority,
        category: Category,
        repeatFrequency: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = userId
            if (currentUserId.isEmpty()) {
                Log.e("TodoViewModel", "User is not authenticated")
                return@launch // Do not add task if no user is authenticated
            }

            // Adjust reminder based on repeat frequency
            val adjustedReminder = when (repeatFrequency) {
                "Daily" -> reminder?.plus(24 * 60 * 60 * 1000) // Add 1 day
                "Weekly" -> reminder?.plus(7 * 24 * 60 * 60 * 1000) // Add 1 week
                "Monthly" -> reminder?.let {
                    Calendar.getInstance().apply {
                        timeInMillis = it
                        add(Calendar.MONTH, 1) // Add 1 month
                    }.timeInMillis
                }
                else -> reminder // No change if no repeat frequency
            }

            // Create new Todo with repeat frequency
            val newTodo = Todo(
                title = title,
                createdAt = Date.from(Instant.now()),
                reminderDate = adjustedReminder,
                isChecked = false,
                priority = priority,
                category = category,
                userId = currentUserId,
                repeatFrequency = repeatFrequency // Save repeat frequency
            )

            // Add the task to the database
            todoDao.addTodo(newTodo)

            // Log for debugging
            Log.d("TodoViewModel", "Task added for userId $currentUserId: $newTodo")

            // Schedule a notification if a reminder is set
            adjustedReminder?.let {
                scheduleNotification(title, it)
            }
        }
    }

    /*
    fun addUser(user: UserObject) {
        viewModelScope.launch(Dispatchers.IO) {
            val newUser = UserObject(
                userId = user.userId,
                username = user.username,
                email = user.email
            )

            // Add the task to the database
            userDao.addUser(newUser)

            // Log for debugging
            Log.d("TodoViewModel", "User added: $newUser")
        }
    }
    */
    fun updateUsername(newUsername: String, mail: String, thisUserId: String, profileImageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (thisUserId.isEmpty()) {
                Log.e("TodoViewModel", "User is not authenticated")
                return@launch // Do not add task if no user is authenticated
            } else {
                val updatedUser = UserObject(
                    userId = thisUserId,
                    username = newUsername,
                    email = mail,
                    profileImageUrl = profileImageUrl
                )

                Log.d("TodoViewModel", "Updating user: $updatedUser")

                userDao.updateUser(updatedUser)
            }

            // Log for debugging
            Log.d("TodoViewModel", "User updated: $thisUserId, New username: $newUsername")
        }
    }

        fun toggleTaskCompletion(todo: Todo) {
            viewModelScope.launch(Dispatchers.IO) {

                val updatedTodo =
                    todo.copy(isFinished = !todo.isFinished, isChecked = !todo.isChecked)
                todoDao.updateTodo(updatedTodo)

                Log.d("TodoViewModel", "Task toggled: $updatedTodo")
            }
        }

        fun deleteTodo(id: Int) {
            viewModelScope.launch(Dispatchers.IO) {
                todoDao.deleteTodo(id)
            }
        }

        fun scheduleNotification(title: String, reminderTime: Long?) {
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

        // Toggle theme and save the preference
        fun toggleTheme() {
            _isDarkMode.value = !_isDarkMode.value
            preferencesManager.saveThemePreference(_isDarkMode.value)
        }
}