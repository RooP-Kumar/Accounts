package com.zen.accounts.presentation.utility

//import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolder
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.inspectable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.data.db.model.ExpenseItem
import com.zen.accounts.presentation.ui.screens.common.LoadingState
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.primary_color
import com.zen.accounts.presentation.ui.theme.shadowColor
import java.util.Date

@Composable
fun Modifier.customShadow(
    elevation: Dp = generalPadding,
    shape: Shape = RoundedCornerShape(generalPadding),
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = shadowColor,
    spotColor: Color = shadowColor,
) = if (elevation > 0.dp || clip) {
    inspectable(
        inspectorInfo = debugInspectorInfo {
            name = "shadow"
            properties["elevation"] = elevation
            properties["shape"] = shape
            properties["clip"] = clip
            properties["ambientColor"] = ambientColor
            properties["spotColor"] = spotColor
        }
    ) {
        graphicsLayer {
            this.shadowElevation = elevation.toPx()
            this.shape = shape
            this.clip = clip
            this.ambientShadowColor = ambientColor
            this.spotShadowColor = spotColor
        }
    }
} else {
    this
}

@Composable
fun Modifier.customClickable(
    showMenu : MutableState<Boolean>,
    onClick : () -> Unit
) : Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.then(Modifier
        .clickable(
            interactionSource = interactionSource,
            indication = if (showMenu.value) null else LocalIndication.current
        ) {
            if(!showMenu.value) {
                onClick()
            } else {
                showMenu.value = !showMenu.value
            }
        }
    )
}

@Composable
fun Modifier.generalBorder(
    color: Color = primary_color,
    borderRadiusX: Dp = generalPadding,
    borderRadiusY: Dp = generalPadding,
    width: Dp = 0.5.dp,
    backgroundColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.background
): Modifier = this
    .drawBehind {

        /**
         * Please use it before background, otherwise it will not work properly.
         * */

        val shadowColor = android.graphics.Color.toArgb(color.value.toLong())
        val widthPx = width.toPx()
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = shadowColor
            it.drawRoundRect(
                -1 * widthPx,
                -1 * widthPx,
                this.size.width + widthPx,
                this.size.height + widthPx,
                borderRadiusX.toPx(),
                borderRadiusY.toPx(),
                paint
            )
        }
    }
    .clip(shape = RoundedCornerShape(generalPadding))
    .background(backgroundColor)

@Composable
fun Modifier.generalCircleBorder(
    size: Dp = 0.5.dp,
    color: Color = shadowColor,
    backgroundColor : Color = androidx.compose.material3.MaterialTheme.colorScheme.background
): Modifier = this
    .drawBehind {

        /**
         * Please use it before background, otherwise it will not work properly.
         * */

        val shadowColor = android.graphics.Color.toArgb(color.value.toLong())
        val radius = size.toPx() / 2
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = shadowColor
            it.drawCircle(
                Offset(radius, radius),
                radius + 2,
                paint
            )
        }
    }
    .clip(CircleShape)
    .background(backgroundColor)

fun Context.toast(msg : String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun<T> List<T>.toArrayList() : ArrayList<T> {
    val temp = arrayListOf<T>()
    temp.addAll(this)
    return temp
}

fun Any.toLoadingState() : LoadingState {
    return if(this::class.simpleName == LoadingState::class.simpleName) {
        this as LoadingState
    } else {
        LoadingState.IDLE
    }
}

fun Any.toHashMap() : HashMap<Long, Boolean> {
    return if(this::class.simpleName == HashMap<Long, Boolean>()::class.simpleName) {
        this as HashMap<Long, Boolean>
    } else {
        hashMapOf()
    }
}

fun Any.toDp() : Dp {
    return if(this::class.simpleName == Dp::class.simpleName) {
        this as Dp
    } else {
        0.dp
    }
}

fun Any.toExpense() : Expense {
    return if(this::class.simpleName == Expense::class.simpleName) {
        this as Expense
    } else {
        Expense(0L, "", arrayListOf(), 0.0, Date())
    }
}

fun Any.toExpenseItem() : ArrayList<ExpenseItem> {
    return if(this::class.simpleName == ArrayList<ExpenseItem>()::class.simpleName) {
        this as ArrayList<ExpenseItem>
    } else {
        arrayListOf(ExpenseItem(0L, "", 0.0, Date()))
    }
}

