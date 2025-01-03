package com.example.didit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onProfileClick: () -> Unit,
    onTasksClick: () -> Unit,
    onFinishedTasksClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { /* Handle drawer or back navigation */ }) {
                Icon(painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = onTasksClick) {
                Icon(painter = painterResource(id = R.drawable.baseline_date_range_24),
                    contentDescription = "Tasks")
            }
            IconButton(onClick = onFinishedTasksClick) {
                Icon(painter = painterResource(id = R.drawable.baseline_checklist_24),
                    contentDescription = "Finished Tasks")
            }
            IconButton(onClick = onProfileClick) {
                Icon(painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = "Profile")
            }
        }
    )
}
@Composable
fun TodoListPage(viewModel: TodoViewModel) {
    val todoList by viewModel.todoList.observeAsState()

    // Debug log to check if the list and reminder date are correct
    Log.d("TodoList", "Todo List: $todoList")

    var showDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    var reminderDate by remember { mutableStateOf<Long?>(null)}


        Scaffold(
        topBar = {
            AppTopBar(
                title = "Todo List",
                onProfileClick = { /* Handle profile click */ },
                onTasksClick = { /* Handle tasks click */ },
                onFinishedTasksClick = { /* Handle finished tasks click */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add Task")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues)
                    .padding(8.dp)
            ) {
                todoList?.let {
                    LazyColumn(
                        content = {
                            itemsIndexed(todoList!!) { _: Int, item: Todo ->
                                TodoItem(
                                    item = item,
                                    onDelete = {
                                        viewModel.deleteTodo(item.id)
                                    },
                                    onCheckedChange = { checked ->
                                        viewModel.toggleTaskChecked(item.copy(isChecked = checked))
                                    }
                                )
                            }
                        }
                    )
                } ?: Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "No tasks yet!"
                )
            }
        }
    )

    // Dialog for Adding a Task
    if (showDialog) {
        TaskCreationDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onAddTask = { title, reminder, priority, category ->
                viewModel.addTodo(title, reminder, priority, category)
                inputText = "" // Clear the input text
                reminderDate = null // Reset reminder date
                showDialog = false // Close the dialog
            })
    }
}

@Composable
fun TaskCreationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddTask: (String, Long?, Priority, String) -> Unit
) {
    if (showDialog) {
        var inputText by remember { mutableStateOf("") }
        var reminderDate by remember { mutableStateOf<Long?>(null) }
        var selectedPriority by remember { mutableStateOf(Priority.LOW) } // Default to LOW
        val priorities = listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH) // Priority options

        var selectedCategory by remember { mutableStateOf("Others") } // Default to "Job"
        val categories = listOf("Job", "Personal", "Hobbies", "Others") // Category options

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
                                    category,
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
    /*val priorityColor = when (item.priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color.Yellow
        Priority.LOW -> Color.Green
    }*/
    //var checked by remember { mutableStateOf(false) }
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
                color = Color.LightGray
            )
            Text(
                text = item.title,
                fontSize = 20.sp,
                color = Color.White
            )
            /*
            Text(
                text = "Priority: ${item.priority.name}",
                fontSize = 14.sp,
                color = priorityColor
            )
            */
            // Display the reminder time if it's set
            item.reminderDate?.let { reminderDate ->
                Text(
                    text = "Reminder: ${
                        SimpleDateFormat(
                            "MM/dd/yyyy HH:mm",
                            Locale.getDefault()
                        ).format(Date(reminderDate))
                    }",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }
        Text(
            if (item.isChecked) "Done!" else "",
            fontSize = 15.sp,
            color = Color.LightGray
        )
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = { checked ->
                //Log.d("Checkbox", "Checkbox clicked: $checked for item: ${item.id}")
                onCheckedChange(checked) // Pass the change to the ViewModel or parent composable
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