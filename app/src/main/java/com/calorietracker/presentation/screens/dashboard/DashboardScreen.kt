package com.calorietracker.presentation.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.PullRefreshIndicatorPadding
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.calorietracker.domain.model.DiaryEntry
import com.calorietracker.domain.model.MealType
import com.calorietracker.presentation.components.common.EmptyState
import com.calorietracker.presentation.components.common.ErrorScreen
import com.calorietracker.presentation.components.common.GlassCard
import com.calorietracker.presentation.components.common.LoadingIndicator
import com.calorietracker.presentation.components.dashboard.DailySummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = viewModel::onRefresh
    )
    
    when {
        uiState.isLoading && uiState.dailySummary == null -> {
            LoadingIndicator(message = "Загрузка...")
        }
        uiState.errorMessage != null -> {
            ErrorScreen(
                message = uiState.errorMessage!!,
                onRetry = viewModel::onRefresh
            )
        }
        else -> {
            DashboardContent(
                dailySummary = uiState.dailySummary,
                calorieNorm = uiState.calorieNorm,
                entriesByMealType = uiState.entriesByMealType,
                isRefreshing = uiState.isRefreshing,
                onRefresh = viewModel::onRefresh,
                onDeleteEntry = viewModel::onDeleteEntry
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    dailySummary: DailySummary?,
    calorieNorm: Int,
    entriesByMealType: Map<MealType, List<DiaryEntry>>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDeleteEntry: (DiaryEntry) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(rememberPullRefreshState(isRefreshing, onRefresh))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // место для BottomNavigationBar
        ) {
            // Заголовок с датой
            item {
                Text(
                    text = "Сегодня",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // Карточка с итогами дня
            if (dailySummary != null) {
                item {
                    DailySummaryCard(
                        dailySummary = dailySummary,
                        calorieNorm = calorieNorm
                    )
                }
            }
            
            // Секции приёмов пищи
            MealType.values().forEach { mealType ->
                val entries = entriesByMealType[mealType] ?: emptyList()
                
                item {
                    MealSection(
                        mealType = mealType,
                        entries = entries,
                        onDeleteEntry = onDeleteEntry
                    )
                }
            }
            
            // Пустое состояние если нет записей
            if (entriesByMealType.isEmpty()) {
                item {
                    EmptyState(
                        title = "Нет записей",
                        message = "Добавьте первый продукт в дневник"
                    )
                }
            }
        }
        
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun MealSection(
    mealType: MealType,
    entries: List<DiaryEntry>,
    onDeleteEntry: (DiaryEntry) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = getMealTypeName(mealType),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (entries.isEmpty()) {
            Text(
                text = "Нет продуктов",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        } else {
            entries.forEach { entry ->
                DiaryEntryItem(
                    entry = entry,
                    onDelete = { onDeleteEntry(entry) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DiaryEntryItem(
    entry: DiaryEntry,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${entry.weightGrams}г • ${entry.nutritionalInfo.calories.toInt()} ккал",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Text(
                text = "Удалить",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

private fun getMealTypeName(mealType: MealType): String {
    return when (mealType) {
        MealType.BREAKFAST -> "Завтрак"
        MealType.LUNCH -> "Обед"
        MealType.DINNER -> "Ужин"
        MealType.SNACK -> "Перекус"
    }
}
