package com.example.didit.db

import androidx.room.TypeConverter
import com.example.didit.Category
import java.util.Date

class Converters {
    @TypeConverter
    fun fromDate(date : Date): Long{
        return date.time
    }

    @TypeConverter
    fun toDate(time : Long) : Date{
        return Date(time)
    }

    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name // Convert the enum to a string
    }

    @TypeConverter
    fun toCategory(categoryName: String): Category {
        return try {
            Category.valueOf(categoryName.uppercase()) // Convert the string to enum, ignoring case
        } catch (e: IllegalArgumentException) {
            // Default to CATEGORY.OTHERS if no match is found
            Category.OTHERS
        }
    }
}

