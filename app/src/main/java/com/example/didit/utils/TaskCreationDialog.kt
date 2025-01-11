package com.example.didit.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.didit.db.Category
import com.example.didit.db.Priority
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun TaskCreationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddTask: (String, Long?, Priority, Category, String?) -> Unit // Added repeatFrequency as a parameter
) {
    if (showDialog) {
        var inputText by remember { mutableStateOf("") }
        var reminderDate by remember { mutableStateOf<Long?>(null) }
        var selectedPriority by remember { mutableStateOf(Priority.LOW) }
        val priorities = listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH)

        var selectedCategory by remember { mutableStateOf(Category.OTHERS) }
        val categories = listOf(Category.PERSONAL, Category.JOB, Category.HOBBIES, Category.OTHERS)

        var repeatFrequency by remember { mutableStateOf<String?>(null) } // Store the repeat frequency

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
                    Spacer(modifier = Modifier.height(4.dp))
                    // Grid layout for category buttons
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // 2 buttons per row
                        horizontalArrangement = Arrangement.spacedBy(8.dp), // Horizontal spacing between buttons
                        verticalArrangement = Arrangement.spacedBy(8.dp), // Vertical spacing between rows
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp) // Padding for better alignment
                    ) {
                        items(categories) { category ->
                            Button(
                                onClick = { selectedCategory = category },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedCategory == category) MaterialTheme.colorScheme.primary else Color.LightGray
                                ),
                                modifier = Modifier
                                    .defaultMinSize(minWidth = 120.dp), // Adjust button width if needed
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(category.name, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Priority:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        priorities.forEach { priority ->
                            val priorityColor = when (priority) {
                                Priority.HIGH -> Color(0xFFF28B82)
                                Priority.MEDIUM -> Color(0xFFFFCC80)
                                Priority.LOW -> Color(0xFF80CBC4)
                            }
                            Button(
                                onClick = { selectedPriority = priority },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedPriority == priority) priorityColor else Color.LightGray
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .defaultMinSize(minWidth = 80.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(priority.name, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Repeat Frequency:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Daily", "Weekly", "Monthly").forEach { frequency ->
                            Button(
                                onClick = { repeatFrequency = frequency },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (repeatFrequency == frequency) MaterialTheme.colorScheme.primary else Color.LightGray
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .defaultMinSize(minWidth = 80.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(frequency, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (inputText.isNotBlank()) {
                        // Adjust the reminder date based on the repeat frequency
                        val adjustedReminderDate = when (repeatFrequency) {
                            "Daily" -> reminderDate?.plus(24 * 60 * 60 * 1000) // Add 1 day
                            "Weekly" -> reminderDate?.plus(7 * 24 * 60 * 60 * 1000) // Add 1 week
                            "Monthly" -> reminderDate?.let {
                                Calendar.getInstance().apply {
                                    timeInMillis = it
                                    add(Calendar.MONTH, 1)
                                }.timeInMillis
                            }
                            else -> reminderDate
                        }

                        onAddTask(inputText, adjustedReminderDate, selectedPriority, selectedCategory, repeatFrequency)
                        onDismiss()
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