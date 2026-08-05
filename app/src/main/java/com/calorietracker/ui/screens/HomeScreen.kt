package com.calorietracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.calorietracker.ui.components.*
import com.calorietracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToAddFood: () -> Unit,
    onNavigateToScanBarcode: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToSettings: () -> Unit,
    @Suppress("UNUSED_PARAMETER") onNavigateToHistory: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale("ru")))
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDarkTheme) {
                        listOf(DarkBackground, DarkSurface)
                    } else {
                        listOf(LightBackground, LightSurface)
                    }
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Сегодня",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                    )
                    Text(
                        text = today.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                    )
                }
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .background(
                            color = if (isDarkTheme) DarkGlassBg else LightGlassBg,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Настройки",
                        tint = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                    )
                }
            }
            
            // Calorie Summary Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                darkTheme = isDarkTheme,
                padding = "24px"
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Калории",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                        )
                        Text(
                            text = "${uiState.goal} ккал",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${uiState.consumed}",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) DarkPrimary else LightPrimary
                        )
                        Text(
                            text = "ккал",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                        )
                    }
                    
                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(
                                color = if (isDarkTheme) DarkGlassBorder else LightGlassBorder,
                                shape = RoundedCornerShape(6.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(
                                    if (uiState.goal > 0) (uiState.consumed.toFloat() / uiState.goal).coerceIn(0f, 1f) else 0f
                                )
                                .height(12.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            if (isDarkTheme) DarkPrimary else LightPrimary,
                                            if (isDarkTheme) DarkPrimaryLight else LightPrimaryLight
                                        )
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                )
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Осталось: ${maxOf(0, uiState.goal - uiState.consumed)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                        )
                    }
                }
            }
            
            // Macros Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MacroGlassCard("Белки", uiState.protein, "г", ProteinColor, isDarkTheme, modifier = Modifier.weight(1f))
                MacroGlassCard("Жиры", uiState.fat, "г", FatsColor, isDarkTheme, modifier = Modifier.weight(1f))
                MacroGlassCard("Углеводы", uiState.carbs, "г", CarbsColor, isDarkTheme, modifier = Modifier.weight(1f))
            }
            
            // Quick Actions
            Text(
                text = "Быстрые действия",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionGlassButton(
                    icon = Icons.Default.QrCodeScanner,
                    label = "Сканер",
                    onClick = onNavigateToScanBarcode,
                    darkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
                ActionGlassButton(
                    icon = Icons.Default.AddCircle,
                    label = "Еда",
                    onClick = onNavigateToAddFood,
                    darkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
                ActionGlassButton(
                    icon = Icons.Default.FitnessCenter,
                    label = "Спорт",
                    onClick = onNavigateToActivity,
                    darkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Recent Entries
            Text(
                text = "Последние записи",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
            )
            
            if (uiState.recentEntries.isEmpty()) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    darkTheme = isDarkTheme,
                    padding = "24px"
                ) {
                    Text(
                        text = "Нет записей за сегодня",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                uiState.recentEntries.forEach { entry ->
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        darkTheme = isDarkTheme,
                        padding = "16px",
                        onClick = { }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = entry.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
                                )
                                Text(
                                    text = "${entry.calories} ккал",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
                                )
                            }
                            Text(
                                text = entry.time,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDarkTheme) DarkTextSecondary.copy(alpha = 0.6f) else LightTextSecondary.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
            
            // Bottom Navigation - точно как в оригинале с FAB кнопкой
            GlassNavigationBar(
                onNavigateToHome = { },
                onNavigateToProducts = { /* навигация к продуктам */ },
                onNavigateToStatistics = { /* навигация к статистике */ },
                onNavigateToProfile = onNavigateToProfile,
                onAddClick = onNavigateToAddFood,
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
private fun MacroGlassCard(label: String, value: Int, unit: String, color: Color, isDarkTheme: Boolean, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier,
        darkTheme = isDarkTheme,
        padding = "16px"
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
            )
            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun ActionGlassButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    darkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(
                color = if (darkTheme) DarkGlassBg else LightGlassBg,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = (if (darkTheme) DarkGlassBorder else LightGlassBorder).copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (darkTheme) DarkPrimary else LightPrimary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (darkTheme) DarkTextPrimary else LightTextPrimary
            )
        }
    }
}

@Composable
private fun GlassNavigationBar(
    onNavigateToHome: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onAddClick: () -> Unit,
    isDarkTheme: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isDarkTheme) DarkSurface else LightSurface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Левая секция: Главная, Продукты
            NavGlassItem(Icons.Default.Home, "Главная", false, onNavigateToHome, isDarkTheme)
            NavGlassItem(Icons.Default.Inventory2, "Продукты", false, onNavigateToProducts, isDarkTheme)
            
            // FAB кнопка в центре со смещением вверх как в оригинале
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .offset(y = (-20).dp)
            ) {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = if (isDarkTheme) DarkPrimary else LightPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Добавить",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            // Правая секция: Статистика, Профиль
            NavGlassItem(Icons.Default.BarChart, "Статистика", false, onNavigateToStatistics, isDarkTheme)
            NavGlassItem(Icons.Default.Person, "Профиль", false, onNavigateToProfile, isDarkTheme)
        }
    }
}

@Composable
private fun NavGlassItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) {
                if (isDarkTheme) DarkPrimary else LightPrimary
            } else {
                if (isDarkTheme) DarkTextSecondary else LightTextSecondary
            },
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) {
                if (isDarkTheme) DarkPrimary else LightPrimary
            } else {
                if (isDarkTheme) DarkTextSecondary else LightTextSecondary
            }
        )
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

    private val _uiState = mutableStateOf(HomeUiState())
    val uiState: State<HomeUiState> = _uiState

    init {
        loadTodayData()
    }

    private fun loadTodayData() {
        // Load data from repositories synchronously
        // This is a simplified example with mock data
        _uiState.value = HomeUiState(
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
