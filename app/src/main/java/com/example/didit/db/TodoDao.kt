package com.example.didit.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.didit.Todo

@Dao
interface TodoDao {

    @Query(
        """
    SELECT * FROM todos 
    ORDER BY 
    CASE 
        WHEN priority = 'HIGH' THEN 1 
        WHEN priority = 'MEDIUM' THEN 2 
        ELSE 3 
    END, 
    createdAt DESC
    """
    )
    fun getAllTodo(): LiveData<List<Todo>>

    @Query("SELECT * FROM todos WHERE category = :category")
    fun getTodosByCategory(category: String): LiveData<List<Todo>>

    @Insert
    fun addTodo(todo : Todo)

    @Update
    fun updateTodo(todo: Todo)

    @Query("Delete FROM todos where id = :id")
    fun deleteTodo(id : Int)
}