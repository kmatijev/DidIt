package com.example.didit.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.didit.Todo

@Dao
interface TodoDao {

    @Query("SELECT * FROM Todo")
    fun getAllTodo() : LiveData<List<Todo>>

    @Insert
    fun addTodo(todo : Todo)

    @Query("Delete FROM Todo where id = :id")
    fun deleteTodo(id : Int)
}