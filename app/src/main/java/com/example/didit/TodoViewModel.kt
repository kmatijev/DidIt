package com.example.didit

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
import com.example.didit.utils.NotificationUtil
import com.example.didit.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class TodoViewModel(application: Application, private val authViewModel: AuthViewModel) : AndroidViewModel(application) {
    private val todoDao = MainApplication.todoDatabase.getTodoDao()

    private val userId: String
        get() = authViewModel.userId

    private val context = application.applicationContext

    private val preferencesManager = PreferencesManager(application)

    // MutableStateFlow to manage the current theme state (light or dark)
    private val _isDarkMode = MutableStateFlow(preferencesManager.getThemePreference())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    //private val _sortOption = MutableLiveData(SortOption.BY_PRIORITY) // Default sorting option
    //private val sortOption: LiveData<SortOption> = _sortOption

    private val _todoList = MediatorLiveData<List<Todo>>()
    var allTodos: LiveData<List<Todo>> = _todoList

    val activeTasks: LiveData<List<Todo>> = allTodos.map { list ->
        list.filter { !it.isFinished }
    }.distinctUntilChanged()

    val finishedTasks: LiveData<List<Todo>> = allTodos.map { list ->
        list.filter { it.isFinished }
    }.distinctUntilChanged()

    init {
        // Observe changes in the logged-in user
        _todoList.addSource(authViewModel.user.asLiveData()) { user ->
            user?.let {
                fetchTodosForUser(it.uid)
            }
        }
        /*
        // Initialize the MediatorLiveData to observe the changes from sortOption and allTodos
        _todoList.addSource(allTodos) { todos ->
            updateSortedList(todos, _sortOption.value)
        }
        _todoList.addSource(sortOption) { sortOption ->
            updateSortedList(allTodos.value, sortOption)
        }
         */
    }

    fun fetchTodosForUser(userId: String) {
        val userTodos = todoDao.getAllTodosSortedByPriority(userId)
        Log.d("TodoViewModel", "Fetching todos for user: $userId")
        _todoList.addSource(userTodos) { todos ->
            _todoList.value = todos
        }
    }

    /*
    fun updateSortOption(sortOption: SortOption) {
        // Use the appropriate sorted query based on the chosen sortOption
        allTodos = when (sortOption) {
            SortOption.BY_PRIORITY -> todoDao.getAllTodosSortedByPriority(userId)
            SortOption.BY_CATEGORY -> todoDao.getAllTodosSortedByCategory(userId)
            SortOption.BY_REMINDER-> todoDao.getAllTodosSortedByReminder(userId)
        }
    }
     */

    fun addTodo(title: String, reminder: Long?, priority: Priority, category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = userId
            if (currentUserId.isEmpty()) {
                Log.e("TodoViewModel", "User is not authenticated")
                return@launch // Do not add task if no user is authenticated
            }

            val newTodo = Todo(
                title = title,
                createdAt = Date.from(Instant.now()),
                reminderDate = reminder,
                isChecked = false,
                priority = priority,
                category = category,
                userId = currentUserId
            )

            // Add the task to the database
            todoDao.addTodo(newTodo)

            // Log for debugging
            Log.d("TodoViewModel", "Task added for userId $currentUserId: $newTodo")

            // Schedule a notification if a reminder is set
            if (reminder != null) {
                scheduleNotification(title, reminder)
            }
        }
    }

    fun toggleTaskCompletion(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {

            val updatedTodo = todo.copy(isFinished = !todo.isFinished, isChecked = !todo.isChecked)
            todoDao.updateTodo(updatedTodo)

            Log.d("TodoViewModel", "Task toggled: $updatedTodo")
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

    // Toggle theme and save the preference
    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
        preferencesManager.saveThemePreference(_isDarkMode.value)
    }
}