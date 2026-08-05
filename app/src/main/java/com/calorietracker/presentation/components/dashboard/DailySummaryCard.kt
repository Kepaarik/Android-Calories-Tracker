package com.calorietracker.presentation.components.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calorietracker.domain.model.DailySummary
import com.calorietracker.presentation.components.common.GlassCard

@Composable
fun DailySummaryCard(
    dailySummary: DailySummary,
    calorieNorm: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Итого за день",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Калории
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${dailySummary.totalCalories} / $calorieNorm ккал",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Калории",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                CircularProgressIndicator(
                    progress = { 
                        if (calorieNorm > 0) dailySummary.totalCalories.toFloat() / calorieNorm else 0f 
                    },
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // БЖУ
            MacroProgressBar(
                label = "Белки",
                current = dailySummary.totalProteins,
                target = dailySummary.targetProteins ?: ((calorieNorm * 0.3) / 4).toInt(),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MacroProgressBar(
                label = "Жиры",
                current = dailySummary.totalFats,
                target = dailySummary.targetFats ?: ((calorieNorm * 0.3) / 9).toInt(),
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            MacroProgressBar(
                label = "Углеводы",
                current = dailySummary.totalCarbs,
                target = dailySummary.targetCarbs ?: ((calorieNorm * 0.4) / 4).toInt(),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun MacroProgressBar(
    label: String,
    current: Double,
    target: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "${current.toInt()} / $target г",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { if (target > 0) current.toFloat() / target else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
