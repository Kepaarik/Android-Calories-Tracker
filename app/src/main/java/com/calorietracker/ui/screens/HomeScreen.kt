package com.calorietracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToAddFood: () -> Unit,
    onNavigateToScanBarcode: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Сегодня") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Главная") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    label = { Text("Добавить") },
                    selected = false,
                    onClick = onNavigateToAddFood
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    label = { Text("Активность") },
                    selected = false,
                    onClick = onNavigateToActivity
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Профиль") },
                    selected = false,
                    onClick = onNavigateToProfile
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calorie Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Калории сегодня",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${uiState.consumed}",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "/ ${uiState.goal} ккал",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    LinearProgressIndicator(
                        progress = if (uiState.goal > 0) uiState.consumed.toFloat() / uiState.goal else 0f,
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                    )
                }
            }
            
            // Macros Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MacroCard("Белки", uiState.protein, "г", modifier = Modifier.weight(1f))
                MacroCard("Жиры", uiState.fat, "г", modifier = Modifier.weight(1f))
                MacroCard("Углеводы", uiState.carbs, "г", modifier = Modifier.weight(1f))
            }
            
            // Quick Actions
            Text(
                text = "Быстрые действия",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionButton(
                    icon = Icons.Default.QrCodeScanner,
                    label = "Сканер",
                    onClick = onNavigateToScanBarcode,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                ActionButton(
                    icon = Icons.Default.AddCircle,
                    label = "Еда",
                    onClick = onNavigateToAddFood,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                ActionButton(
                    icon = Icons.Default.FitnessCenter,
                    label = "Тренировка",
                    onClick = onNavigateToActivity,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
            
            // Recent Entries
            Text(
                text = "Последние записи",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            if (uiState.recentEntries.isEmpty()) {
                Text(
                    text = "Нет записей за сегодня",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                uiState.recentEntries.forEach { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = entry.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${entry.calories} ккал",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = entry.time,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroCard(label: String, value: Int, unit: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

data class HomeUiState(
    val consumed: Int = 0,
    val goal: Int = 2000,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val recentEntries: List<RecentEntry> = emptyList()
)

data class RecentEntry(
    val name: String,
    val calories: Int,
    val time: String
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodRepository: com.calorietracker.data.repository.FoodRepository,
    private val goalRepository: com.calorietracker.data.repository.GoalRepository
) : ViewModel() {

    val uiState = MutableStateFlow(HomeUiState())

    init {
        loadTodayData()
    }

    private fun loadTodayData() {
        viewModelScope.launch {
            // Load data from repositories
            // This is a simplified example
            uiState.value = uiState.value.copy(
                consumed = 1250,
                protein = 85,
                fat = 45,
                carbs = 150,
                recentEntries = listOf(
                    RecentEntry("Овсянка", 350, "08:30"),
                    RecentEntry("Яблоко", 95, "10:15"),
                    RecentEntry("Куриная грудка", 165, "13:00")
                )
            )
        }
    }
}
