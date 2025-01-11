package com.example.didit.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.didit.utils.AppTopBar
import com.example.didit.db.Category
import com.example.didit.R
import com.example.didit.db.SortOption
import com.example.didit.db.Todo
import com.example.didit.utils.TaskCreationDialog
import com.example.didit.utils.TodoItem
import com.example.didit.viewmodels.TodoViewModel

@Composable
fun TodoListPage(
    viewModel: TodoViewModel,
    onFinishedTasksClick: () -> Unit,
    onProfileClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    val activeTasks by viewModel.activeTasks.observeAsState(emptyList())

    // Sorting state
    var selectedSortOption by remember { mutableStateOf(SortOption.BY_PRIORITY) } // Default sort option4
    var selectedFilterOption by remember { mutableStateOf(Category.ALL) } // Default sort option
    var isSortingDropdownExpanded by remember { mutableStateOf(false) } // For controlling the dropdown visibility
    var isFilteringDropdownExpanded by remember { mutableStateOf(false) } // For controlling the dropdown visibility

    val categories = listOf(
        Category.ALL,
        Category.JOB,
        Category.PERSONAL,
        Category.HOBBIES,
        Category.OTHERS
    )


    // Function to sort the list based on the selected option
    val sortedTodoList = when (selectedSortOption) {
        SortOption.BY_REMINDER -> activeTasks.sortedBy { it.reminderDate }
        SortOption.BY_CATEGORY -> activeTasks.sortedBy { it.category.name }
        SortOption.BY_PRIORITY -> activeTasks.sortedBy { it.priority.sortOrder }
    }

    val filteredTodoList = remember(selectedFilterOption, sortedTodoList) {
        if (selectedFilterOption == Category.ALL) sortedTodoList
        else
        {
            Log.d("TodoListPage", "Filtering by $selectedFilterOption")
            sortedTodoList.filter { it.category == selectedFilterOption }
        }
    }

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    //Log.d("SortedTodoList", "Sorted by $selectedSortOption: $sortedTodoList")

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Todo List",
                onProfileClick = onProfileClick,
                onStatisticsClick = onStatisticsClick,
                onTasksClick = {},
                onFinishedTasksClick = onFinishedTasksClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add Task"
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)},
        content = { paddingValues ->
            if (filteredTodoList.isEmpty()) {
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
            } else
            {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(paddingValues)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sort By:")
                        Box(
                            modifier = Modifier.width(100.dp)
                        ) {
                            TextButton(onClick = { isSortingDropdownExpanded = true }) {
                                Text(text = when (selectedSortOption) {
                                    SortOption.BY_PRIORITY -> "PRIORITY"
                                    SortOption.BY_CATEGORY -> "CATEGORY"
                                    SortOption.BY_REMINDER -> "REMINDER"
                                },
                                    maxLines = 1, // Ensures single-line text
                                    textAlign = TextAlign.Center)// Center-align text
                            }
                            DropdownMenu(
                                expanded = isSortingDropdownExpanded,
                                onDismissRequest = { isSortingDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("PRIORITY") },
                                    onClick = {
                                        selectedSortOption = SortOption.BY_PRIORITY
                                        isSortingDropdownExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("CATEGORY") },
                                    onClick = {
                                        selectedSortOption = SortOption.BY_CATEGORY
                                        isSortingDropdownExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("REMINDER TIME") },
                                    onClick = {
                                        selectedSortOption = SortOption.BY_REMINDER
                                        isSortingDropdownExpanded = false
                                    }
                                )
                            }
                        }

                        //Spacer(modifier = Modifier.width(16.dp))

                        Text(text = "Filter by: ")

                        Box(
                            modifier = Modifier.width(100.dp)
                        ){
                            TextButton(
                                onClick = { isFilteringDropdownExpanded = true },
                            ) {
                                Text(text = selectedFilterOption.name)
                            }

                            DropdownMenu(
                                expanded = isFilteringDropdownExpanded,
                                onDismissRequest = { isFilteringDropdownExpanded = false },
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(
                                            category.name,
                                            maxLines = 1, // Ensures single-line text
                                            textAlign = TextAlign.Center) },
                                        onClick = {
                                            selectedFilterOption = category
                                            isFilteringDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    // Display tasks
                    if (filteredTodoList.isNotEmpty()) {
                        LazyColumn(
                            content = {
                                itemsIndexed(filteredTodoList) { _: Int, item: Todo ->
                                    TodoItem(
                                        item = item,
                                        onDelete = {
                                            viewModel.deleteTodo(item.id)
                                        },
                                        onCheckedChange = { checked -> viewModel.toggleTaskCompletion(item)
                                            snackbarMessage = "Task ${item.title} done!"
                                            showSnackbar = true
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    )
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false // Reset snackbar state
        }
    }

    // Dialog for Adding a Task
    if (showDialog) {
        TaskCreationDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onAddTask = { title, reminder, priority, category, repeatFrequency ->
                if (reminder != null) {
                    viewModel.addTodo(title, reminder, priority, category, repeatFrequency)
                }
                else
                {
                    viewModel.addTodo(title, null, priority, category, null)
                }
                showDialog = false // Close the dialog
            }
        )
    }
}