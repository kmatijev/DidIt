package com.example.didit.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun DateTimePicker(
    onDateTimeSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val now = Calendar.getInstance() // Current date and time

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

                        val selectedTimeInMillis = calendar.timeInMillis
                        if (selectedTimeInMillis > now.timeInMillis) {
                            // Selected time is in the future
                            onDateTimeSelected(selectedTimeInMillis)
                        } else {
                            // Show a toast message for invalid selection
                            Toast.makeText(
                                context,
                                "Please select a future date and time.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
