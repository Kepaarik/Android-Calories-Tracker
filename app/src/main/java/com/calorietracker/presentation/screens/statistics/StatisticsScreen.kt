package com.calorietracker.presentation.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.calorietracker.presentation.components.common.EmptyState
import com.calorietracker.presentation.components.common.ErrorScreen
import com.calorietracker.presentation.components.common.GlassCard
import com.calorietracker.presentation.components.common.LoadingIndicator

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when {
        uiState.isLoading -> {
            LoadingIndicator(message = "Загрузка статистики...")
        }
        uiState.errorMessage != null -> {
            ErrorScreen(
                message = uiState.errorMessage!!,
                onRetry = viewModel::loadStatistics
            )
        }
        else -> {
            StatisticsContent(
                weeklyData = uiState.weeklyCalories,
                dailyMacros = uiState.dailyMacros
            )
        }
    }
}

@Composable
private fun StatisticsContent(
    weeklyData: List<WeeklyCalorieData>,
    dailyMacros: DailyMacrosData?
) {
    var selectedPeriod by remember { mutableIntStateOf(0) }
    val periods = listOf("День", "Неделя", "Месяц")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Статистика",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Переключатель периода
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            periods.forEachIndexed { index, period ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = periods.size),
                    onClick = { selectedPeriod = index },
                    selected = selectedPeriod == index
                ) {
                    Text(period)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (weeklyData.isEmpty()) {
            EmptyState(
                title = "Нет данных",
                message = "Добавляйте записи в дневник для просмотра статистики"
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // График калорий за неделю
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Калории за неделю",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Простая визуализация графика
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            weeklyData.forEach { day ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val height = ((day.calories / 3000f) * 100.dp)
                                        .coerceIn(10.dp, 100.dp)
                                    
                                    androidx.compose.foundation.Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.1f)
                                            .height(height)
                                            .then(
                                                Modifier.background(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                                )
                                            )
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = day.dayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Макронутриенты за день
                if (dailyMacros != null) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "БЖУ за сегодня",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            MacroStatRow(
                                label = "Белки",
                                value = dailyMacros.proteins,
                                unit = "г",
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            MacroStatRow(
                                label = "Жиры",
                                value = dailyMacros.fats,
                                unit = "г",
                                color = MaterialTheme.colorScheme.secondary
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            MacroStatRow(
                                label = "Углеводы",
                                value = dailyMacros.carbs,
                                unit = "г",
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroStatRow(
    label: String,
    value: Double,
    unit: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Text(
            text = "${value.toInt()} $unit",
            style = MaterialTheme.typography.bodyLarge,
            color = color
        )
    }
}

data class WeeklyCalorieData(
    val dayName: String,
    val calories: Double
)

data class DailyMacrosData(
    val proteins: Double,
    val fats: Double,
    val carbs: Double
)
