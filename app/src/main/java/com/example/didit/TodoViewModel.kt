package com.example.didit

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

    fun addTodo(title: String, reminderDate: Long?){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.addTodo(Todo(
                title = title,
                createdAt = Date.from(Instant.now()),
                reminderDate = reminderDate, // Pass the reminder date
                isChecked = false // Set the initial state of the checkbox to unchecked
            ))
        }
    }

    fun deleteTodo(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteTodo(id)
        }
    }

}