package com.calorietracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calorietracker.ui.theme.*

/**
 * Glass-карточка с эффектом размытия фона (backdrop blur)
 * Вдохновлена дизайном из проекта Calorie-tracker
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = false,
    onClick: (() -> Unit)? = null,
    padding: String = "16dp",
    content: @Composable () -> Unit
) {
    val backgroundColor = if (darkTheme) DarkSurface else LightSurface
    val borderColor = if (darkTheme) DarkGlassBorder else LightGlassBorder
    
    Box(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                when (padding) {
                    "32px" -> 32.dp
                    "24px" -> 24.dp
                    "20px" -> 20.dp
                    else -> 16.dp
                }
            )
    ) {
        content()
    }
}

/**
 * Glass-кнопка с эффектом размытия
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    darkTheme: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default,
    enabled: Boolean = true,
    fullWidth: Boolean = false
) {
    val baseColors = getButtonColors(variant, darkTheme)
    
    Box(
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .clickable(enabled = enabled) { onClick() }
            .alpha(if (enabled) 1f else 0.5f)
            .background(
                color = baseColors.background,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = baseColors.border,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = text,
            color = baseColors.text,
            fontWeight = FontWeight.Medium
        )
    }
}

enum class ButtonVariant {
    Default, Success, Danger, Icon
}

data class ButtonColors(
    val background: Color,
    val border: Color,
    val text: Color
)

@Composable
private fun getButtonColors(variant: ButtonVariant, darkTheme: Boolean): ButtonColors {
    return when (variant) {
        ButtonVariant.Success -> {
            if (darkTheme) {
                ButtonColors(
                    background = Color(0xFF43A047).copy(alpha = 0.7f),
                    border = Color.White.copy(alpha = 0.2f),
                    text = Color.White
                )
            } else {
                ButtonColors(
                    background = Color(0xFF388E3C).copy(alpha = 0.95f),
                    border = Color(0xFF2E7D32).copy(alpha = 0.3f),
                    text = Color.White
                )
            }
        }
        ButtonVariant.Danger -> {
            if (darkTheme) {
                ButtonColors(
                    background = Color(0xFFEF5350).copy(alpha = 0.7f),
                    border = Color.White.copy(alpha = 0.2f),
                    text = Color.White
                )
            } else {
                ButtonColors(
                    background = Color(0xFFD32F2F).copy(alpha = 0.95f),
                    border = Color(0xFFC62828).copy(alpha = 0.3f),
                    text = Color.White
                )
            }
        }
        ButtonVariant.Icon -> {
            ButtonColors(
                background = if (darkTheme) DarkGlassBg else LightGlassBg,
                border = if (darkTheme) DarkGlassBorder else LightGlassBorder,
                text = if (darkTheme) DarkTextPrimary else LightTextPrimary
            )
        }
        ButtonVariant.Default -> {
            ButtonColors(
                background = if (darkTheme) DarkGlassBg else LightGlassBg,
                border = if (darkTheme) DarkGlassBorder else LightGlassBorder,
                text = if (darkTheme) DarkTextPrimary else LightTextPrimary
            )
        }
    }
}

/**
 * Glass-поле ввода
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    darkTheme: Boolean = false,
    singleLine: Boolean = true
) {
    val bgColor = if (darkTheme) DarkGlassBg else LightGlassBg
    val borderColor = if (darkTheme) DarkGlassBorder else LightGlassBorder
    
    Box(
        modifier = modifier
            .background(
                color = bgColor,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        androidx.compose.material3.TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                androidx.compose.material3.Text(
                    text = placeholder,
                    color = (if (darkTheme) DarkTextSecondary else LightTextSecondary).copy(alpha = 0.7f)
                )
            },
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = if (darkTheme) DarkPrimary else LightPrimary
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = if (darkTheme) DarkTextPrimary else LightTextPrimary
            ),
            modifier = Modifier.fillMaxSize(),
            singleLine = singleLine
        )
    }
}
