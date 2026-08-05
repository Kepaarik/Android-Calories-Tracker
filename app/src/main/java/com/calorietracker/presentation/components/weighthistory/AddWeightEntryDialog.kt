package com.calorietracker.presentation.components.weighthistory

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.calorietracker.presentation.components.common.GlassTextField
import java.time.LocalDate

@Composable
fun AddWeightEntryDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (weightKg: Double, date: LocalDate) -> Unit
) {
    var weightText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Добавить запись о весе",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                GlassTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = "Вес (кг)",
                    placeholder = "75.5",
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Здесь можно добавить DatePicker
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Отмена")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val weight = weightText.toDoubleOrNull()
                            if (weight != null && weight > 0) {
                                onConfirm(weight, selectedDate)
                            }
                        },
                        enabled = weightText.toDoubleOrNull() != null && weightText.toDoubleOrNull()!! > 0
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
}
