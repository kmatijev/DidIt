package com.example.didit.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.didit.CategoryStats
import com.example.didit.Todo

@Dao
interface TodoDao {
    /*
    //@Query("SELECT * FROM todos WHERE userId = :userId ORDER BY createdAt DESC")
    //fun getAllTodosSortedByReminder(userId: String): LiveData<List<Todo>>

    //@Query("SELECT * FROM todos WHERE userId = :userId ORDER BY category ASC")
    //fun getAllTodosSortedByCategory(userId: String): LiveData<List<Todo>>

    //Query("SELECT * FROM todos WHERE userId = :userId AND category = :category")
    //fun getTodosByUserAndCategory(userId: Int, category: String): LiveData<List<Todo>>

    // Get all todos for a specific user
    @Query("SELECT * FROM todos WHERE userId = :userId")
    fun getAllTodo(userId: String): LiveData<List<Todo>>

    @Query("SELECT * FROM todos")
    fun getAllTodo(): LiveData<List<Todo>>
     */

    @Query("SELECT * FROM todos WHERE userId = :userId")
    fun getAllTodos(userId: String): LiveData<List<Todo>>

    @Insert
    suspend fun addTodo(todo : Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Query("Delete FROM todos where id = :id")
    suspend fun deleteTodo(id : Int)

    // Get total tasks for a user
    @Query("SELECT COUNT(*) FROM todos WHERE userId = :userId")
    fun getTotalTasks(userId: String): LiveData<Int>

    // Get completed tasks for a user
    @Query("SELECT COUNT(*) FROM todos WHERE userId = :userId AND isFinished = TRUE")
    fun getCompletedTasks(userId: String): LiveData<Int>

    // Get task counts grouped by category
    @Query("""
        SELECT Category, COUNT(*) as count
        FROM todos 
        WHERE userId = :userId 
        AND isFinished = 1
        GROUP BY Category
    """)
    fun getCategoryStats(userId: String): LiveData<List<CategoryStats>>
}