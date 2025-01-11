package com.example.didit.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.didit.R
import com.example.didit.db.Todo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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