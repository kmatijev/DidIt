package com.example.didit.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.didit.Todo

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY reminderDate ASC")
    fun getTodosSortedByReminder(): LiveData<List<Todo>>

    @Query("SELECT * FROM todos ORDER BY category ASC")
    fun getTodosSortedByCategory(): LiveData<List<Todo>>

    @Query("SELECT * FROM todos ORDER BY priority DESC")
    fun getTodosSortedByPriority(): LiveData<List<Todo>>

    @Query("SELECT * FROM todos")
    fun getAllTodo(): LiveData<List<Todo>>

    @Insert
    fun addTodo(todo : Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Query("Delete FROM todos where id = :id")
    fun deleteTodo(id : Int)
}