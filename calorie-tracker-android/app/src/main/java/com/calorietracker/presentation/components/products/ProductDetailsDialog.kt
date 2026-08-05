package com.calorietracker.presentation.components.products

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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calorietracker.domain.model.MealType
import com.calorietracker.domain.model.Product
import com.calorietracker.presentation.components.common.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToDiary: (MealType, Int) -> Unit
) {
    var selectedMealType by remember { mutableStateOf(MealType.BREAKFAST) }
    var weightGrams by remember { mutableStateOf(100) }
    
    val calories = (product.caloriesPer100g * weightGrams / 100).toInt()
    val proteins = (product.proteinsPer100g * weightGrams / 100).toInt()
    val fats = (product.fatsPer100g * weightGrams / 100).toInt()
    val carbs = (product.carbsPer100g * weightGrams / 100).toInt()
    
    GlassCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Информация о продукте на 100г
            Column {
                Text(
                    text = "На 100г:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "${product.caloriesPer100g} ккал | Б: ${product.proteinsPer100g}г | Ж: ${product.fatsPer100g}г | У: ${product.carbsPer100g}г",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Выбор приёма пищи
            Text(
                text = "Приём пищи:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                Button(
                    onClick = { expanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                ) {
                    Text(
                        text = when (selectedMealType) {
                            MealType.BREAKFAST -> "Завтрак"
                            MealType.LUNCH -> "Обед"
                            MealType.DINNER -> "Ужин"
                            MealType.SNACK -> "Перекус"
                        }
                    )
                }
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    MealType.values().forEach { mealType ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = when (mealType) {
                                        MealType.BREAKFAST -> "Завтрак"
                                        MealType.LUNCH -> "Обед"
                                        MealType.DINNER -> "Ужин"
                                        MealType.SNACK -> "Перекус"
                                    }
                                )
                            },
                            onClick = {
                                selectedMealType = mealType
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Выбор веса
            Text(
                text = "Вес: $weightGrams г",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Slider(
                value = weightGrams.toFloat(),
                onValueChange = { weightGrams = it.toInt() },
                valueRange = 10f..500f,
                steps = 48,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Итоговая информация
            GlassCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Итого: $calories ккал",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Б: ${proteins}г", style = MaterialTheme.typography.bodySmall)
                        Text("Ж: ${fats}г", style = MaterialTheme.typography.bodySmall)
                        Text("У: ${carbs}г", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Отмена")
                }
                Button(
                    onClick = {
                        onAddToDiary(selectedMealType, weightGrams)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Добавить")
                }
            }
        }
    }
}
