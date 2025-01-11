package com.example.didit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map

class StatisticsViewModel(application: Application, authViewModel: AuthViewModel) : AndroidViewModel(application) {

    private val todoDao = MainApplication.todoDatabase.getTodoDao()
    private val userId = authViewModel.userId

    // Expose LiveData for tasks and category stats
    var completedTasks: LiveData<Int> = MutableLiveData()
    var totalTasks: LiveData<Int> = MutableLiveData()

    init {
        fetchData(userId)
    }

    fun fetchData(userId: String) {
        // Fetch the total tasks, completed tasks, and category stats for the user
        totalTasks = todoDao.getTotalTasks(userId)
        completedTasks = todoDao.getCompletedTasks(userId)
    }

    fun getCategoryStats(userId: String): LiveData<Map<Category, Int>> {
        return todoDao.getCategoryStats(userId).map { list ->
            // Convert the list to a map
            list.associate { it.category to it.count }
        }
    }
}
