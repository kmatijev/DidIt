package com.example.didit.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.didit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onProfileClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onTasksClick: () -> Unit,
    onFinishedTasksClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {},
        actions = {
            IconButton(onClick = onTasksClick) {
                Icon(painter = painterResource(id = R.drawable.baseline_date_range_24),
                    contentDescription = "Tasks")
            }
            IconButton(onClick = onFinishedTasksClick) {
                Icon(painter = painterResource(id = R.drawable.baseline_checklist_24),
                    contentDescription = "Finished Tasks")
            }
            IconButton(onClick = onStatisticsClick) {
                Icon(painter = painterResource(id = R.drawable.baseline_bar_chart_24),
                    contentDescription = "Task Statistics")
            }
            IconButton(onClick = onProfileClick) {
                Icon(painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = "Profile")
            }
        }
    )
}