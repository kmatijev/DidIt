package com.example.didit

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class Priority(val sortOrder: Int) {
    HIGH(1),
    MEDIUM(2),
    LOW(3)
}

enum class Category {
    JOB, PERSONAL, HOBBIES, OTHERS
}

// Enum class for sorting options
enum class SortOption {
    BY_PRIORITY,
    BY_CATEGORY,
    BY_REMINDER
}

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String,
    var createdAt: Date,
    var reminderDate: Long? = null,
    var isChecked: Boolean = false,
    var priority: Priority,
    val category: Category = Category.OTHERS,
    val isFinished: Boolean = false, // Add this new field to track finished tasks
    val userId: String // Foreign key to link the task to a specific user
)
