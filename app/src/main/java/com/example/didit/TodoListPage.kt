package com.example.didit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TodoListPage(
    viewModel: TodoViewModel,
    onFinishedTasksClick: () -> Unit,
    onProfileClick: () -> Unit
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
        Category.OTHERS)


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
            onAddTask = { title, reminder, priority, category ->
                viewModel.addTodo(title, reminder, priority, category)
                showDialog = false // Close the dialog
            }
        )
    }
}



@Composable
fun TaskCreationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddTask: (String, Long?, Priority, Category) -> Unit
) {
    if (showDialog) {
        var inputText by remember { mutableStateOf("") }
        var reminderDate by remember { mutableStateOf<Long?>(null) }
        var selectedPriority by remember { mutableStateOf(Priority.LOW) } // Default to LOW
        val priorities = listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH) // Priority options

        var selectedCategory by remember { mutableStateOf(Category.OTHERS) } // Default to "Job"
        val categories = listOf(Category.PERSONAL, Category.JOB, Category.HOBBIES, Category.OTHERS) // Category options

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Add Task") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Task Title") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DateTimePicker(onDateTimeSelected = { selectedDateTime ->
                        reminderDate = selectedDateTime
                    })
                    Spacer(modifier = Modifier.height(16.dp))

                    reminderDate?.let {
                        Text("Reminder set for: ${SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault()).format(it)}")
                    }


                    Text("Category:")
                    Spacer(modifier = Modifier.height(8.dp))
                    // Category Selection
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { category ->
                            Button(
                                onClick = { selectedCategory = category },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedCategory == category) MaterialTheme.colorScheme.primary else Color.LightGray
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .defaultMinSize(minWidth = 80.dp), // Ensures buttons are wide enough
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp) // Fine-tune padding
                            ) {
                                Text(
                                    category.name,
                                    color = Color.White,
                                    fontSize = 14.sp, // Adjust font size to prevent wrapping
                                    maxLines = 1, // Restrict to a single line
                                    overflow = TextOverflow.Ellipsis // Truncate if the text is too long
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Priority:")
                    Spacer(modifier = Modifier.height(8.dp))
                    // Priority Selection
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        priorities.forEach { priority ->
                            val priorityColor = when (priority) {
                                Priority.HIGH -> Color(0xFFF28B82) // Light Coral
                                Priority.MEDIUM -> Color(0xFFFFCC80) // Pale Orange
                                Priority.LOW -> Color(0xFF80CBC4) // Soft Teal
                            }
                            Button(
                                onClick = { selectedPriority = priority },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedPriority == priority) priorityColor else Color.LightGray
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .defaultMinSize(minWidth = 80.dp), // Ensures buttons are wide enough
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp) // Fine-tune padding
                            ) {
                                Text(
                                    priority.name,
                                    color = Color.White,
                                    fontSize = 14.sp, // Adjust font size to prevent wrapping
                                    maxLines = 1, // Restrict to a single line
                                    overflow = TextOverflow.Ellipsis // Truncate if the text is too long
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (inputText.isNotBlank()) {
                        onAddTask(inputText, reminderDate, selectedPriority, selectedCategory)
                        onDismiss() // Close the dialog
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}



@Composable
fun TodoItem(item: Todo, onDelete: () -> Unit, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = SimpleDateFormat("HH:mm, MM/dd/yyyy", Locale.ENGLISH).format(item.createdAt),
                fontSize = 10.sp,
                color = Color.White
            )
            Text(
                text = item.title,
                fontSize = 20.sp,
                color = Color.White
            )
            item.reminderDate?.let { reminderDate ->
                Text(
                    text = "Reminder: ${
                        SimpleDateFormat(
                            "MM/dd/yyyy HH:mm",
                            Locale.getDefault()
                        ).format(Date(reminderDate))
                    }",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
        Text(
            if (item.isChecked) "Done!" else "",
            fontSize = 15.sp,
            color = Color.White
        )
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = { checked ->
                onCheckedChange(checked) // Trigger ViewModel update
            },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,
                uncheckedColor = Color.White,
                checkmarkColor = Color.Black
            )
        )
        // Delete button
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_24),
                contentDescription = "Delete",
                tint = Color.White
            )
        }
    }
}

@Composable
fun DateTimePicker(
    onDateTimeSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    // Show DatePickerDialog
    Button(onClick = {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                // Show TimePickerDialog after selecting the date
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        onDateTimeSelected(calendar.timeInMillis)  // Return selected date and time
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }) {
        Text("Set Reminder")
    }
}