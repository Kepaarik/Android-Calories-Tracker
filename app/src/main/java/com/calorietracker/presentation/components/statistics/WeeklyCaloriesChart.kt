package com.calorietracker.presentation.components.statistics

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyleProducer
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasureContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.ChartValues
import com.patrykandpatrick.vico.core.cartesian.data.line
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlin.math.roundToInt

@Composable
fun WeeklyCaloriesChart(
    caloriesByDay: Map<Int, Double>,
    modifier: Modifier = Modifier
) {
    val chartModelProducer = remember { CartesianChartModelProducer() }
    
    val shapeComponent = rememberShapeComponent(
        shape = Shape.Pill,
        color = androidx.compose.ui.graphics.Color(0xFF0077B3)
    )
    
    val textComponent = rememberTextComponent(
        color = androidx.compose.ui.graphics.Color.White,
        textSize = 12.sp,
        background = shapeComponent,
        padding = 8.dp,
        textAlign = TextComponent.TextAlign.Center
    )
    
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(
                    rememberLineSpec(
                        lineColor = androidx.compose.ui.graphics.Color(0xFF0077B3),
                        lineThickness = 4.dp,
                        point = null
                    )
                )
            ),
            startAxis = rememberStartAxis(
                label = rememberAxisLabelComponent(),
                guideline = null
            ),
            bottomAxis = rememberBottomAxis(
                label = rememberAxisLabelComponent(),
                valueFormatter = { value, _ -> 
                    when (value.toInt()) {
                        1 -> "Пн"
                        2 -> "Вт"
                        3 -> "Ср"
                        4 -> "Чт"
                        5 -> "Пт"
                        6 -> "Сб"
                        7 -> "Вс"
                        else -> ""
                    }
                }
            ),
            style = m3ChartStyleProducer()
        ),
        modelProducer = chartModelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        horizontalLayout = HorizontalLayout.zoomable(
            minScale = 1f,
            maxScale = 5f
        )
    ) { chartValues ->
        chartModelProducer.runTransaction {
            clear()
            line {
                caloriesByDay.forEach { (day, calories) ->
                    value(day.toDouble(), calories.roundToInt().toDouble())
                }
            }
        }
    }
}
