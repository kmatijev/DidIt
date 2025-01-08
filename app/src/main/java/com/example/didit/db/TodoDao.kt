package com.example.didit.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.didit.Todo

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllTodosSortedByReminder(userId: String): LiveData<List<Todo>>

    @Query("SELECT * FROM todos WHERE userId = :userId ORDER BY category ASC")
    fun getAllTodosSortedByCategory(userId: String): LiveData<List<Todo>>

    @Query("SELECT * FROM todos WHERE userId = :userId ORDER BY priority DESC")
    fun getAllTodosSortedByPriority(userId: String): LiveData<List<Todo>>

    /*
    // Get all todos for a specific user
    @Query("SELECT * FROM todos WHERE userId = :userId")
    fun getAllTodo(userId: String): LiveData<List<Todo>>

    @Query("SELECT * FROM todos")
    fun getAllTodo(): LiveData<List<Todo>>
     */

    @Insert
    suspend fun addTodo(todo : Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Query("Delete FROM todos where id = :id")
    suspend fun deleteTodo(id : Int)
}