package com.zen.accounts.presentation.utility

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.zen.accounts.R
import com.zen.accounts.data.db.model.ExpenseItem
import com.zen.accounts.presentation.ui.screens.common.getRupeeString
import com.zen.accounts.presentation.ui.theme.Typography
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.light_enabled_color

@Composable
fun ExpenseItemLayout(
    expenseItem: ExpenseItem,
    textStyle: TextStyle = Typography.bodyLarge.copy(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = generalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = expenseItem.itemTitle,
                style = textStyle
            )
        }

        Text(
            text = getRupeeString(expenseItem.itemAmount ?: 0.0),
            style = textStyle,
            modifier = Modifier
                .wrapContentWidth()
        )
    }
}

// Function to Get Quantity
fun getQtyInString(qty : Int) : String {
    if(qty == 0) {
        return ""
    }
    return "qty x $qty"
}

@Composable
fun LoadingScreen() {
    var screenLoadingRotation by remember { mutableFloatStateOf(0f) }
    val screenInfiniteTransition = rememberInfiniteTransition(label = "")
    screenLoadingRotation = screenInfiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    ).value

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sync),
                contentDescription = "Rotating Icon",
                tint = light_enabled_color,
                modifier = Modifier
                    .size(48.dp)
                    .graphicsLayer {
                        rotationZ = screenLoadingRotation
                    }
            )
        }
    }
}