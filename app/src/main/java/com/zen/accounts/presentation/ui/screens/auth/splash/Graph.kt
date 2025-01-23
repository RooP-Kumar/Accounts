package com.zen.accounts.presentation.ui.screens.auth.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.zen.accounts.R
import com.zen.accounts.presentation.ui.screens.common.daily
import com.zen.accounts.presentation.ui.screens.common.getRupeeString
import com.zen.accounts.presentation.ui.screens.common.monthly
import com.zen.accounts.presentation.ui.screens.common.weekly
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.halfGeneralPadding
import com.zen.accounts.presentation.ui.theme.primary_color
import kotlin.math.ceil

@Composable
fun GraphLayout(
    yAxis : List<Double>?,
    xAxis : List<String>?,
    todayDataList : List<Double>?,
    filterType : String,
    showMonthlyProgressbar: Boolean,
    onFilterClicked : (String) -> Unit
) {
    
    val activeIndex = remember { mutableIntStateOf(-1) }
    Column(
        modifier = Modifier
            .padding(horizontal = generalPadding)
            .clip(shape = RoundedCornerShape(generalPadding))
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(generalPadding)
            .padding(end = 4.dp) // only for last x-axis indicator to look better when active.
    ) {
        TopLayout(filterType, onFilterClicked)
        
            BottomLayout(
                xAxis = xAxis,
                yAxis = yAxis,
                todayDataList = todayDataList,
                activeIndex = activeIndex,
                showMonthlyProgressbar = showMonthlyProgressbar
            )
    }
}


@Composable
private fun ColumnScope.TopLayout(
    filterType : String,
    onFilterClicked : (String) -> Unit
) {
    Box(
        modifier = Modifier
            .align(Alignment.End)
            .padding(bottom = generalPadding),
        contentAlignment = Alignment.CenterEnd
    ) {
        val filterButtonModifierForBorderAndBackground = if (isSystemInDarkTheme()) Modifier.border(
            width = 0.5.dp,
            color = primary_color,
            shape = RoundedCornerShape(generalPadding)
        ) else Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        
        var showGraphTypeOption by remember { mutableStateOf(false) }
        var selectedGraphType by remember { mutableStateOf(filterType) }
        
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(generalPadding))
                .clickable {
                    showGraphTypeOption = true
                }
                .then(filterButtonModifierForBorderAndBackground)
                .padding(horizontal = generalPadding, vertical = halfGeneralPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedGraphType,
                textAlign = TextAlign.Right,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_drop_down),
                contentDescription = "Filter icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = halfGeneralPadding)
            )
        }
        
        DropdownMenu(
            expanded = showGraphTypeOption,
            onDismissRequest = { showGraphTypeOption = false }) {
            repeat(3) { count ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (count == 0) daily else if (count == 1) weekly else monthly,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    onClick = {
                        selectedGraphType =
                            if (count == 0) daily else if (count == 1) weekly else monthly
                        showGraphTypeOption = false
                        onFilterClicked(selectedGraphType)
                    }
                )
            }
        }
        
    }
}

@Composable
private fun BottomLayout(
    yAxis : List<Double>?,
    xAxis : List<String>?,
    todayDataList : List<Double>?,
    activeIndex : MutableIntState,
    showMonthlyProgressbar : Boolean
) {
    val maxPoint : Double = (yAxis?.max() ?: 100.0) + 100
    val parentWidth = remember { mutableFloatStateOf(0f) }
    val parentHeight = remember { mutableFloatStateOf(0f) }
    
    Row {
        // Y-axis rupees indicator layout
        YAxisRupeesLayout(parentHeight = parentHeight.floatValue, maxPoint = maxPoint)
        
        // Line chart and below months layout
        Column(
            modifier = Modifier
        ) {
            
            // Line chart layout
            LineChartLayout(
                parentHeight = parentHeight,
                parentWidth = parentWidth,
                xAxis = xAxis,
                yAxis = yAxis,
                todayDataList,
                maxPoint = maxPoint,
                activeIndex,
                showMonthlyProgressbar
            )
            
            // Months layout
            Box(
                modifier = Modifier
                    .padding(top = halfGeneralPadding)
            ) {
                if (xAxis != null) {
                    for (i in xAxis.indices) {
                        var textWidth by remember {
                            mutableFloatStateOf(0f)
                        }
                        Text(
                            text = xAxis[i],
                            style = TextStyle(
                                color = if (activeIndex.intValue != i) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                                fontSize = TextUnit(12f, TextUnitType.Sp)
                            ),
                            modifier = Modifier
                                .onGloballyPositioned {
                                    textWidth = it.size.width.toFloat()
                                }
                                .offset {
                                    IntOffset(
                                        (i * parentWidth.floatValue / (xAxis.size - 1) - textWidth / 2).toInt(),
                                        0
                                    )
                                }
                                .clip(shape = RoundedCornerShape(generalPadding))
                                .clickable {
                                    activeIndex.intValue = if (activeIndex.intValue == i) -1 else i
                                }
                                .then(
                                    if (activeIndex.intValue == i) Modifier.background(MaterialTheme.colorScheme.primary)
                                    else Modifier
                                )
                                .padding(
                                    horizontal = generalPadding.minus(6.dp),
                                    vertical = halfGeneralPadding.minus(5.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YAxisRupeesLayout(
    parentHeight : Float,
    maxPoint : Double
) {
    Box(
        modifier = Modifier
            .padding(end = generalPadding.times(2))
    ) {
        // 6 partition of chart's height
        for (i in 0..5) {
            val y = parentHeight - parentHeight / 5 * i
            var yHeight by remember {
                mutableIntStateOf(0)
            }
            Text(
                text = if (i == 0) getRupeeString(0.0, showZero = true) else getRupeeString(
                    ceil(maxPoint / 5 * i)
                ),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = TextUnit(11f, TextUnitType.Sp)
                ),
                modifier = Modifier
                    .onGloballyPositioned {
                        yHeight = it.size.height
                    }
                    .offset {
                        IntOffset(0, (y - yHeight / 2).toInt())
                    }
            )
        }
    }
}

@Composable
private fun LineChartLayout(
    parentHeight : MutableFloatState,
    parentWidth : MutableFloatState,
    xAxis : List<String>?,
    yAxis : List<Double>?,
    todayDataList : List<Double>?,
    maxPoint : Double,
    activeIndex : MutableIntState,
    showMonthlyProgressbar : Boolean
) {
    if (xAxis != null && yAxis != null) {
        // Line chart gradient color from line to bottom
        val gradientColors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            if (isSystemInDarkTheme()) MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.background.copy(
                alpha = 0.3f
            )
        )
        
        val currentDensity = LocalDensity.current
        val fontFamilyResolver = LocalFontFamilyResolver.current
        val textMeasurer by remember {
            mutableStateOf(
                TextMeasurer(
                    defaultDensity = currentDensity,
                    defaultLayoutDirection = LayoutDirection.Ltr,
                    defaultFontFamilyResolver = fontFamilyResolver
                )
            )
        }
        
        val rememberRipple = rememberInfiniteTransition(label = "Current ripple effect")
        
        val animateRadius = rememberRipple.animateFloat(
            initialValue = 0f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "Animate radius"
        )
        
        val animateAlpha = rememberRipple.animateFloat(
            initialValue = 0.5f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    1000,
                    easing = LinearEasing
                )
            ), label = "Animate alpha"
        )
        
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .onGloballyPositioned {
                    parentWidth.floatValue = it.size.width.toFloat()
                    parentHeight.floatValue = it.size.height.toFloat()
                }
                .drawBehind {
                    var startX : Float
                    var startY : Float
                    val widthPart = parentWidth.floatValue / (xAxis.size - 1)
                    val innerPointPart = parentHeight.floatValue / 24
                    var heightPart = parentHeight.floatValue / 5
                    
                    // Horizontal lines
                    startX = 0f
                    for (i in 0 .. (if(todayDataList != null) xAxis.size - 1 else 5)) {
                        startY = heightPart * i
                        drawLine(
                            brush = Brush.linearGradient(listOf(Color.Black, Color.Black)),
                            start = Offset(startX, startY),
                            end = Offset(parentWidth.floatValue, startY),
                            strokeWidth = 0.3f,
                            pathEffect = PathEffect.dashPathEffect(FloatArray(5) { 25f })
                        )
                    }
                    
                    // Dot on the points
                    heightPart = (parentHeight.floatValue) / maxPoint.toFloat()
                    
                    // Graph lines from one dot to another dot
                    val linePath = Path()
                    startY = parentHeight.floatValue - (yAxis[0].toFloat() * heightPart)
                    linePath.moveTo(0f, startY)
                    for (i in 1 until yAxis.size) {
                        startX = widthPart * i
                        startY = parentHeight.floatValue - (heightPart * yAxis[i].toFloat())
                        linePath.cubicTo(
                            startX - widthPart / 2,
                            (parentHeight.floatValue - (heightPart * yAxis[i - 1].toFloat())),
                            startX - widthPart / 2,
                            startY,
                            startX,
                            startY
                        )
                    }
                    
                    val backgroundPath = linePath.copy()
                    backgroundPath.lineTo(
                        (yAxis.size - 1) * widthPart,
                        parentHeight.floatValue + 200
                    )
                    backgroundPath.lineTo(0f, parentHeight.floatValue + 200)
                    backgroundPath.lineTo(
                        0f,
                        parentHeight.floatValue - heightPart * yAxis[0].toFloat()
                    )
                    drawPath(
                        linePath,
                        brush = Brush.linearGradient(listOf(primary_color, primary_color)),
                        style = Stroke(width = 1f)
                    )
                    drawPath(
                        backgroundPath,
                        brush = Brush.linearGradient(
                            gradientColors,
                            start = Offset(parentWidth.floatValue / 2, 0f),
                            end = Offset(parentWidth.floatValue / 2, parentHeight.floatValue * 1.2f)
                        )
                    )
                    
                    for (i in yAxis.indices) {
                        startX = widthPart * i
                        startY = parentHeight.floatValue - (heightPart * yAxis[i].toFloat())
                        
                        // Detail rectangle
                        if (activeIndex.intValue == i) {
                            val textSize = textMeasurer.measure(
                                getRupeeString(yAxis[i], true), TextStyle(
                                    fontFamily = FontFamily(Font(R.font.montserrat))
                                )
                            ).size
                            val rectSize =
                                Size(
                                    textSize.width.toFloat() + 70f,
                                    textSize.height.toFloat() + 50f
                                )
                            drawRoundRect(
                                color = primary_color,
                                topLeft = Offset(
                                    x =
                                    if (startX + 30 + rectSize.width < parentWidth.floatValue)
                                        if (startY + rectSize.height / 2 < parentHeight.floatValue)
                                            startX + 30
                                        else
                                            if (i > 0)
                                                startX - rectSize.width / 2
                                            else
                                                startX - 30
                                    else if (startY + rectSize.height / 2 < parentHeight.floatValue)
                                        startX - rectSize.width - 30
                                    else
                                        startX - rectSize.width + 30,
                                    y =
                                    if (startY + rectSize.height / 2 < parentHeight.floatValue)
                                        startY - rectSize.height / 2
                                    else
                                        startY - rectSize.height - 30
                                ),
                                size = rectSize,
                                cornerRadius = CornerRadius(20f, 20f)
                                
                            )
                            
                            drawText(
                                textMeasurer = textMeasurer,
                                style = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.montserrat)),
                                    color = Color.White
                                ),
                                text = getRupeeString(yAxis[i], true),
                                topLeft = Offset(
                                    x =
                                    if (startX + 30 + rectSize.width < parentWidth.floatValue)
                                        if (startY + rectSize.height / 2 < parentHeight.floatValue)
                                            startX + 30 + (rectSize.width / 2 - textSize.width / 2)
                                        else
                                            if (i > 0)
                                                startX - textSize.width / 2
                                            else
                                                startX
                                    else if (startY + rectSize.height / 2 < parentHeight.floatValue)
                                        startX - 30 - textSize.width - (rectSize.width / 2 - textSize.width / 2)
                                    else
                                        startX - textSize.width + 30 - (rectSize.width / 2 - textSize.width / 2),
                                    y =
                                    if (startY + rectSize.height / 2 < parentHeight.floatValue)
                                        startY - textSize.height / 2
                                    else
                                        startY - 30 - rectSize.height / 2 - textSize.height / 2
                                )
                            )
                            
                        }
                        
                        // Center dots
                        drawCircle(
                            brush = Brush.linearGradient(
                                listOf(
                                    primary_color,
                                    primary_color
                                )
                            ),
                            radius = 5f,
                            center = Offset(startX, startY)
                        )
                        
                        
                        if (i == yAxis.size - 1)
                            drawCircle(
                                color = primary_color.copy(alpha = animateAlpha.value),
                                radius = animateRadius.value,
                                center = Offset(startX, startY),
                            )
                        
                        // Circle around center dot
                        if (activeIndex.intValue == i) {
                            drawCircle(
                                brush = Brush.linearGradient(
                                    listOf(
                                        primary_color,
                                        primary_color
                                    )
                                ),
                                center = Offset(startX, startY),
                                radius = 20f,
                                style = Stroke(3f)
                            )
                        }
                        
                        // Vertical line from point to bottom.
                        if (activeIndex.intValue == i) {
                            drawLine(
                                brush = Brush.linearGradient(
                                    listOf(
                                        primary_color,
                                        primary_color
                                    )
                                ),
                                strokeWidth = 3f,
                                start = Offset(startX, startY),
                                end = Offset(startX, parentHeight.floatValue + 22f)
                            )
                        }
                    }
                    
                    
                }
        ) {
            for (i in yAxis.indices) {
                var dotSize by remember {
                    mutableStateOf(IntSize(0, 0))
                }
                val posX = remember {
                    derivedStateOf {
                        (i * parentWidth.floatValue / (xAxis.size - 1) - dotSize.width / 2).toInt()
                    }
                }
                
                val posY = remember {
                    derivedStateOf {
                        (parentHeight.floatValue - (parentHeight.floatValue / maxPoint) * yAxis[i] - dotSize.height / 2f).toInt()
                    }
                }
                
                // Details box inside chart layout
                
                // For creating point clickable and do some stuff on click.
                Spacer(
                    modifier = Modifier
                        .size(25.dp)
                        .onGloballyPositioned {
                            dotSize = it.size
                        }
                        .offset {
                            IntOffset(
                                x = posX.value,
                                y = posY.value
                            )
                        }
                        .clip(CircleShape)
                        .clickable {
                            activeIndex.intValue =
                                if (activeIndex.intValue == i) -1 else i // setting clicked point into activeIndex so that I can easily enable information of this point.
                        }
                )
            }
            
            
        }
        
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .onGloballyPositioned {
                    parentWidth.floatValue = it.size.width.toFloat()
                    parentHeight.floatValue = it.size.height.toFloat()
                },
            contentAlignment = Alignment.Center
        ) {
            if(showMonthlyProgressbar) {
                CircularProgressIndicator()   
            } else {
                Text(
                    text = "No Data",
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}
