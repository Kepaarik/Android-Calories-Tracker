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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.calorietracker.presentation.components.dashboard.MealSection
import com.calorietracker.presentation.components.dashboard.DiaryEntryItem
import com.calorietracker.presentation.components.dashboard.WaterTrackerWidget
import com.calorietracker.presentation.components.dashboard.WeightTrackerWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onAddEntryClick: () -> Unit = {},
    onWeightHistoryClick: () -> Unit = {},
    onAddWaterClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = viewModel::loadDiaryEntries
    )
    val snackbarHostState = remember { androidx.compose.material3.ScaffoldSnackbarState() }
    
    when {
        uiState.isLoading && uiState.dailySummary == null -> {
            LoadingIndicator(message = "Загрузка...")
        }
        uiState.error != null -> {
            ErrorScreen(
                message = uiState.error!!,
                onRetry = viewModel::loadDiaryEntries
            )
        }
        else -> {
            // Обработка Undo через Snackbar
            LaunchedEffect(uiState.isUndoAvailable) {
                if (uiState.isUndoAvailable && uiState.lastDeletedEntry != null) {
                    val result = snackbarHostState.showSnackbar(
                        message = "Запись удалена",
                        actionLabel = "Отменить",
                        duration = SnackbarDuration.Long
                    )
                    
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoDeleteEntry()
                    } else {
                        viewModel.clearUndoState()
                    }
                }
            }
            
            DashboardContent(
                uiState = uiState,
                isRefreshing = uiState.isLoading,
                onRefresh = viewModel::loadDiaryEntries,
                onDeleteEntry = viewModel::deleteDiaryEntry,
                onAddEntryClick = onAddEntryClick,
                onWeightHistoryClick = onWeightHistoryClick,
                onAddWaterClick = onAddWaterClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDeleteEntry: (Int) -> Unit,
    onAddEntryClick: () -> Unit,
    onWeightHistoryClick: () -> Unit,
    onAddWaterClick: () -> Unit
) {
    val snackbarHostState = remember { androidx.compose.material3.ScaffoldSnackbarState() }
    
    LaunchedEffect(uiState.isUndoAvailable) {
        if (uiState.isUndoAvailable && uiState.lastDeletedEntry != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Запись удалена",
                actionLabel = "Отменить",
                duration = SnackbarDuration.Long
            )
            
            if (result == SnackbarResult.ActionPerformed) {
                onDeleteEntry(uiState.lastDeletedEntry!!.id) // Восстановить запись
            } else {
                // Пользователь не нажал "Отменить", запись уже удалена в БД
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                uiState.dailySummary?.let { summary ->
                    item {
                        DailySummaryCard(
                            dailySummary = summary,
                            calorieNorm = 2000 // TODO: получить из профиля
                        )
                    }
                }
                
                // Секции приёмов пищи
                item {
                    MealSection(
                        mealType = MealType.BREAKFAST,
                        entries = uiState.breakfastEntries,
                        onDeleteEntry = onDeleteEntry,
                        onAddEntryClick = onAddEntryClick
                    )
                }
                
                item {
                    MealSection(
                        mealType = MealType.LUNCH,
                        entries = uiState.lunchEntries,
                        onDeleteEntry = onDeleteEntry,
                        onAddEntryClick = onAddEntryClick
                    )
                }
                
                item {
                    MealSection(
                        mealType = MealType.DINNER,
                        entries = uiState.dinnerEntries,
                        onDeleteEntry = onDeleteEntry,
                        onAddEntryClick = onAddEntryClick
                    )
                }
                
                item {
                    MealSection(
                        mealType = MealType.SNACK,
                        entries = uiState.snackEntries,
                        onDeleteEntry = onDeleteEntry,
                        onAddEntryClick = onAddEntryClick
                    )
                }
                
                // Виджет воды
                item {
                    WaterTrackerWidget(
                        currentIntakeMl = uiState.waterIntakeMl,
                        targetMl = uiState.targetWaterMl,
                        onAddWater = onAddWaterClick,
                        onRemoveWater = { /* TODO */ }
                    )
                }
                
                // Виджет веса
                uiState.latestWeight?.let { weight ->
                    item {
                        WeightTrackerWidget(
                            currentWeight = weight.weightKg,
                            onWeightHistoryClick = onWeightHistoryClick
                        )
                    }
                }
                
                // Пустое состояние если нет записей
                if (uiState.diaryEntries.isEmpty()) {
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
                state = rememberPullRefreshState(isRefreshing, onRefresh),
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
