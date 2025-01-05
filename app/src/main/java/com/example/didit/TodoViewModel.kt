package com.example.didit

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val todoDao = MainApplication.todoDatabase.getTodoDao()
    private val context = application.applicationContext

    private val _sortOption = MutableLiveData<SortOption>(SortOption.BY_PRIORITY) // Default sorting option
    private val sortOption: LiveData<SortOption> = _sortOption

    private val _todoList = MediatorLiveData<List<Todo>>()

    // Fetch unsorted list (all todos)
    private val allTodos: LiveData<List<Todo>> = todoDao.getAllTodo()

    val activeTasks: LiveData<List<Todo>> = allTodos.map { list ->
        list.filter { !it.isFinished }
    }.distinctUntilChanged()

    val finishedTasks: LiveData<List<Todo>> = allTodos.map { list ->
        list.filter { it.isFinished }
    }.distinctUntilChanged()

    init {
        // Initialize the MediatorLiveData to observe the changes from sortOption and allTodos
        _todoList.addSource(allTodos) { todos ->
            updateSortedList(todos, _sortOption.value)
        }
        _todoList.addSource(sortOption) { sortOption ->
            updateSortedList(allTodos.value, sortOption)
        }
    }

    private fun updateSortedList(todos: List<Todo>?, sortOption: SortOption?) {
        if (todos == null || sortOption == null) return
        _todoList.value = when (sortOption) {
            SortOption.BY_PRIORITY -> todos.sortedBy { it.priority.sortOrder }
            SortOption.BY_CATEGORY -> todos.sortedBy { it.category.name }
            SortOption.BY_REMINDER -> todos.sortedBy { it.reminderDate }
        }
    }

    fun addTodo(title: String, reminder: Long?, priority: Priority, category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            // Create the new task object
            val newTodo = Todo(
                title = title,
                createdAt = Date.from(Instant.now()),
                reminderDate = reminder, // Pass the reminder date
                isChecked = false, // Set the initial state of the checkbox to unchecked
                priority = priority,
                category = category
            )

            // Add the task to the database
            todoDao.addTodo(newTodo)

            // Ensure the task is added correctly
            Log.d("ViewModel", "Task added: $newTodo")

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

    private val preferencesManager = PreferencesManager(application)

    // MutableStateFlow to manage the current theme state (light or dark)
    private val _isDarkMode = MutableStateFlow(preferencesManager.getThemePreference())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // Toggle theme and save the preference
    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
        preferencesManager.saveThemePreference(_isDarkMode.value)
    }
}