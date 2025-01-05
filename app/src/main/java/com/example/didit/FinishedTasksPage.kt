package com.example.didit


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun FinishedTasksPage(
    viewModel: TodoViewModel,
    onTasksClick: () -> Unit,
    onBackClick: () -> Unit // This will be passed to handle the back navigation
){
    val finishedTasks by viewModel.finishedTasks.observeAsState(emptyList())



    Scaffold(
        topBar = {
            AppTopBar(
                title = "Finished Tasks",
                onProfileClick = { /* Handle profile click */ },
                onTasksClick = onTasksClick,
                onFinishedTasksClick = { /* Handle finished tasks click */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back to Previous Page"
                )
            }
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