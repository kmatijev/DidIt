package com.example.didit

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.didit.db.User
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


/*
@Entity(
    tableName = "todos",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE // Optionally, delete tasks if the user is deleted
        )
    ]
)*/
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
    val isFinished: Boolean = false // Add this new field to track finished tasks
    //val userId: Long // Foreign key to link the task to a specific user
)
