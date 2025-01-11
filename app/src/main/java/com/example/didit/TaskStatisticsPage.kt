package com.example.didit

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaskStatisticsPage(
    statisticsViewModel: StatisticsViewModel,
    authViewModel: AuthViewModel,
    onFinishedTasksClick: () -> Unit,
    onProfileClick: () -> Unit,
    onTasksClick: () -> Unit
) {
    val userId = authViewModel.userId

    LaunchedEffect(userId) {
        statisticsViewModel.fetchData(userId)
    }

    val completedTasks by statisticsViewModel.completedTasks.observeAsState(0f)
    val totalTasks by statisticsViewModel.totalTasks.observeAsState(0f)
    val categoryStats by statisticsViewModel.getCategoryStats(userId).observeAsState(emptyMap())

    val sortedCategoryStats = categoryStats.toList().sortedByDescending { (category, _) ->
        if (category == Category.OTHERS) Int.MIN_VALUE else 1
    }.toMap()

    // Calculate the task completion percentage
    val taskCompletionPercentage = if (totalTasks == 0) 0f else (completedTasks.toFloat() / totalTasks.toFloat()) * 100

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Statistics",
                onProfileClick = onProfileClick,
                onStatisticsClick = {},
                onTasksClick = onTasksClick,
                onFinishedTasksClick = onFinishedTasksClick
            )
        },
        content = { paddingValues ->
            if (totalTasks == 0) {
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .fillMaxSize() // Fills the screen
                        .padding(16.dp), // Optional: Adds some padding around the icon
                    contentAlignment = Alignment.Center // Centers the icon in the middle
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_inbox_24),
                        contentDescription = "No tasks to display",
                        modifier = Modifier.size(76.dp).align(Alignment.Center),
                    )
                }
            }
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Task Completion Progress (Circular Progress)
                    Text(
                        text = "Task Completion",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (totalTasks == 0) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_date_range_24),
                            contentDescription = "Tasks"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No tasks available")
                    } else {
                        CircularProgressIndicator(
                            progress = { taskCompletionPercentage / 100f },
                            modifier = Modifier.size(150.dp),
                            color = MaterialTheme.colorScheme.primary, // This is the completed task color
                            strokeWidth = 12.dp, // Increase the thickness
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), // Customize the incomplete part color
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${taskCompletionPercentage.toInt()}% Completed", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Stats (Bar Chart)
                    Text(
                        text = "Category Completion Stats",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    CategoryBarChart(
                        sortedCategoryStats,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        })
}
