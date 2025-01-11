package com.example.didit.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.didit.utils.AppTopBar
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
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "No finished tasks yet!"
                    )
                }
            }

        }
    )

}