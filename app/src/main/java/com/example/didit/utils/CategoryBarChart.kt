package com.example.didit.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.didit.db.Category

@Composable
fun CategoryBarChart(
    categoryStats: Map<Category, Int>,
    modifier: Modifier = Modifier,
    colors: Map<Category, Color> = mapOf(
        Category.JOB to Color(0xFF2196F3),
        Category.PERSONAL to Color(0xFFFFC107),
        Category.HOBBIES to Color(0xFF4CAF50),
        Category.OTHERS to Color(0xEEEB4034)
    )
) {
    val maxCount = categoryStats.values.maxOrNull() ?: 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        categoryStats.forEach { (category, count) ->
            val percentage = if (maxCount > 0) count / maxCount.toFloat() else 0f
            val color = colors[category] ?: Color.Gray

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percentage)
                            .fillMaxHeight()
                            .background(color)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
