package com.calorietracker.presentation.components.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calorietracker.domain.model.MealType
import com.calorietracker.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsDialog(
    product: Product,
    selectedMealType: MealType,
    weightGrams: Int,
    onMealTypeChange: (MealType) -> Unit,
    onWeightChange: (Int) -> Unit,
    onAddToDiary: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "На 100г:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Калории: ${product.caloriesPer100g.toInt()} ккал")
                Text("Белки: ${product.proteinsPer100g}г")
                Text("Жиры: ${product.fatsPer100g}г")
                Text("Углеводы: ${product.carbsPer100g}г")
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "Приём пищи:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    MealType.values().forEach { mealType ->
                        TextButton(
                            onClick = { onMealTypeChange(mealType) },
                            colors = if (selectedMealType == mealType)
                                androidx.compose.material3.ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            else
                                androidx.compose.material3.ButtonDefaults.textButtonColors()
                        ) {
                            Text(
                                when (mealType) {
                                    MealType.BREAKFAST -> "Завтрак"
                                    MealType.LUNCH -> "Обед"
                                    MealType.DINNER -> "Ужин"
                                    MealType.SNACK -> "Перекус"
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = weightGrams.toString(),
                    onValueChange = { 
                        val weight = it.toIntOrNull() ?: 0
                        onWeightChange(weight.coerceIn(1, 10000))
                    },
                    label = { Text("Вес (г)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    TextButton(
                        onClick = onAddToDiary,
                        enabled = weightGrams > 0
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
}
