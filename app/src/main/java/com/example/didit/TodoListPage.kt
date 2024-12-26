package com.example.didit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
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
                                TodoItem(item, onDelete = {
                                    viewModel.deleteTodo(item.id)
                                })
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
            onAddTask = { title, reminder ->
                viewModel.addTodo(title)
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
    onAddTask: (String, Long?) -> Unit
) {
    if (showDialog) {
        var inputText by remember { mutableStateOf("") }
        var reminderDate by remember { mutableStateOf<Long?>(null) }

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
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (inputText.isNotBlank()) {
                        onAddTask(inputText, reminderDate)
                        inputText = ""  // Clear input text
                        reminderDate = null  // Reset reminder date
                        onDismiss()  // Close the dialog
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
fun TodoItem(item: Todo, onDelete : ()-> Unit){
    var checked by remember { mutableStateOf(false) }
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
            Text(text = SimpleDateFormat("HH:mm, MM/dd/yyyy",
                Locale.ENGLISH).format(item.createdAt),
                fontSize = 10.sp,
                color = Color.LightGray)
            Text(text = item.title,
                fontSize = 20.sp,
                color = Color.White)
        }
        Text(
            if (checked) "Done!" else "",
            fontSize = 15.sp,
            color = Color.LightGray
        )
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,      // Color when the box is checked
                uncheckedColor = Color.White,   // Color when the box is unchecked
                checkmarkColor = Color.Black    // Color of the checkmark inside the box
            )
        )
        IconButton(onClick = onDelete)
        {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_24),
                contentDescription = "Delete",
                tint = Color.White)
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