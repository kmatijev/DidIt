package com.example.didit

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String,
    var createdAt: Date,
    var reminderDate: Long? = null,
    var isChecked: Boolean = false,  // Add isChecked property
    var priority: Priority = Priority.LOW,
    val category: String
)
