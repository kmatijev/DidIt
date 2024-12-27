package com.example.didit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class TodoViewModel : ViewModel() {

    private val todoDao = MainApplication.todoDatabase.getTodoDao()

    val todoList : LiveData<List<Todo>> = todoDao.getAllTodo()

    fun addTodo(title: String, reminder: Long?){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.addTodo(Todo(
                title = title,
                createdAt = Date.from(Instant.now()),
                reminderDate = reminder, // Pass the reminder date
                isChecked = false // Set the initial state of the checkbox to unchecked
            ))
        }
    }

    fun toggleTaskChecked(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            //val newTodo = todo.copy(isChecked = todo.isChecked)
            //Log.d("ViewModel", "Updating todo: $newTodo")
            todoDao.updateTodo(todo.copy(isChecked = todo.isChecked)) // Update the 'isChecked' field
        }
    }

    fun deleteTodo(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteTodo(id)
        }
    }
}