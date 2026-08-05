package com.calorietracker.presentation.screens.weighthistory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.calorietracker.domain.model.WeightEntry
import com.calorietracker.presentation.components.common.GlassCard
import com.calorietracker.presentation.components.common.ErrorScreen
import com.calorietracker.presentation.components.common.LoadingIndicator
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightHistoryScreen(
    viewModel: WeightHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onAddWeightEntry: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddWeightEntryDialog(
            onDismissRequest = { showAddDialog = false },
            onConfirm = { weight, date ->
                viewModel.addWeightEntry(weight, date)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История веса") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить запись"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }
                
                uiState.error != null -> {
                    ErrorScreen(
                        message = uiState.error!!,
                        onRetry = viewModel::loadWeightHistory
                    )
                }
                
                uiState.weightEntries.isEmpty() -> {
                    EmptyWeightHistory(
                        modifier = Modifier.align(Alignment.Center),
                        onAddClick = { showAddDialog = true }
                    )
                }
                
                else -> {
                    WeightHistoryList(
                        entries = uiState.weightEntries,
                        onDeleteEntry = viewModel::deleteWeightEntry,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyWeightHistory(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Нет записей о весе",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Нажмите + чтобы добавить первую запись",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddClick) {
            Text("Добавить запись")
        }
    }
}

@Composable
fun WeightHistoryList(
    entries: List<WeightEntry>,
    onDeleteEntry: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = entries.sortedByDescending { it.date },
            key = { it.id }
        ) { entry ->
            WeightHistoryItem(
                entry = entry,
                onDelete = { onDeleteEntry(entry.id) }
            )
        }
    }
}

@Composable
fun WeightHistoryItem(
    entry: WeightEntry,
    onDelete: () -> Unit
) {
    GlassCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = entry.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${entry.weightKg} кг",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
