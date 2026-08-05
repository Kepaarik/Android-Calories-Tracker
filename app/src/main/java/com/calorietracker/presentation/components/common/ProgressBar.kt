package com.calorietracker.presentation.components.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.3f),
    progressColor: Brush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0077B3), Color(0xFF00A8CC))
    ),
    cornerRadius: Dp = 6.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500)
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(animatedProgress)
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(progressColor)
        )
    }
}
