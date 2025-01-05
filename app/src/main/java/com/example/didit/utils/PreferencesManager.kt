package com.example.didit.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun saveThemePreference(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply()
    }

    fun getThemePreference(): Boolean {
        return sharedPreferences.getBoolean("isDarkMode", false) // Default is light mode
    }
}
