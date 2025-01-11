package com.example.didit.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.didit.R
import com.example.didit.utils.AppTopBar
import com.example.didit.utils.TodoItem
import com.example.didit.viewmodels.TodoViewModel

@Composable
fun FinishedTasksPage(
    viewModel: TodoViewModel,
    onTasksClick: () -> Unit, // This will be passed to handle the back navigation
    onProfileClick: () -> Unit,
    onStatisticsClick: () -> Unit
){
    val finishedTasks by viewModel.finishedTasks.observeAsState(emptyList())

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Finished Tasks",
                onProfileClick = onProfileClick,
                onStatisticsClick = onStatisticsClick,
                onTasksClick = onTasksClick,
                onFinishedTasksClick = {}
            )
        },
        content = { paddingValues ->
            if (finishedTasks.isEmpty()) {
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(paddingValues)
                        .padding(8.dp)
                ) {
                    if (finishedTasks.isNotEmpty()) {
                        LazyColumn {
                            items(finishedTasks) { task ->
                                TodoItem(
                                    item = task,
                                    onDelete = { viewModel.deleteTodo(task.id) },
                                    onCheckedChange = { viewModel.toggleTaskCompletion(task) }
                                )
                            }
                        }
                    } else {
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
                }
            }
        }
    )
}