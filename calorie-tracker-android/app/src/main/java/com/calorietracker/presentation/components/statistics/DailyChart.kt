package com.calorietracker.presentation.components.statistics

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DailyChart(
    modifier: Modifier = Modifier
) {
    // Заготовка для графика - будет интегрирована с Vico или AndroidPlot
    Text(
        text = "График за день",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier.fillMaxWidth()
    )
}
