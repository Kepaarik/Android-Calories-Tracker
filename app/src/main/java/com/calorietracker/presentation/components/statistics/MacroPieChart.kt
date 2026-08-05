package com.calorietracker.presentation.components.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyleProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.column
import kotlin.math.roundToInt

@Composable
fun DailyMacrosChart(
    proteins: Float,
    fats: Float,
    carbs: Float,
    modifier: Modifier = Modifier
) {
    val chartModelProducer = remember { CartesianChartModelProducer() }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Proteins
        MacroIndicator(
            label = "Белки",
            value = proteins,
            color = Color(0xFF4CAF50)
        )
        
        // Fats
        MacroIndicator(
            label = "Жиры",
            value = fats,
            color = Color(0xFFFFC107)
        )
        
        // Carbs
        MacroIndicator(
            label = "Углеводы",
            value = carbs,
            color = Color(0xFF2196F3)
        )
    }
}

@Composable
private fun MacroIndicator(
    label: String,
    value: Float,
    color: Color
) {
    androidx.compose.material3.Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Text(
            text = label,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = androidx.compose.ui.unit.TextUnit.Companion.Sp(14)
            ),
            color = Color.Gray
        )
        androidx.compose.material3.Text(
            text = "${value.roundToInt()}г",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = androidx.compose.ui.unit.TextUnit.Companion.Sp(20),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = color
        )
    }
}
