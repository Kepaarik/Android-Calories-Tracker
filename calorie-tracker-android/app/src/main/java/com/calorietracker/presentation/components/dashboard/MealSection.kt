package com.calorietracker.presentation.components.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calorietracker.domain.model.MealType
import com.calorietracker.presentation.components.common.GlassCard

@Composable
fun MealSection(
    mealType: MealType,
    entries: List<com.calorietracker.domain.model.DiaryEntry>,
    onAddProduct: (MealType) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCalories = entries.sumOf { it.calories }.toInt()
    val title = when (mealType) {
        MealType.BREAKFAST -> "Завтрак"
        MealType.LUNCH -> "Обед"
        MealType.DINNER -> "Ужин"
        MealType.SNACK -> "Перекус"
    }

    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$title ($totalCalories ккал)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onAddProduct(mealType) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить продукт"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (entries.isNotEmpty()) {
                entries.forEach { entry ->
                    DiaryEntryItem(
                        entry = entry,
                        onDelete = { }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                LinearProgressIndicator(
                    progress = { totalCalories.coerceAtMost(1000) / 1000f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
            } else {
                Text(
                    text = "Нет записей",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
