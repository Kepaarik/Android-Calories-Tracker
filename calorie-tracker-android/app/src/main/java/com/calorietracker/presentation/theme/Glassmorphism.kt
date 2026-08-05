package com.calorietracker.presentation.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Модификатор для создания эффекта Liquid Glass (стекломорфизм)
 */
fun Modifier.glassEffect(
    color: Color = Color.White.copy(alpha = 0.1f),
    blurRadius: Dp = 20.dp,
    borderRadius: Dp = 16.dp
): Modifier = this
    .then(
        Modifier
            .blur(blurRadius)
    )

// Примечание: полноценная реализация glassmorphism требует использования
// GraphicsLayer и кастомного рисования. Эта функция - базовая заготовка.
// Полная реализация будет добавлена в компоненте GlassCard
