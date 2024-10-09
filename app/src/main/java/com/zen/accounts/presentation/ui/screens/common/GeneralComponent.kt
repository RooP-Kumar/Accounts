package com.zen.accounts.presentation.ui.screens.common

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zen.accounts.R
import com.zen.accounts.presentation.ui.theme.Typography
import com.zen.accounts.presentation.ui.theme.disabled_color
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.green_color
import com.zen.accounts.presentation.ui.theme.halfGeneralPadding
import com.zen.accounts.presentation.ui.theme.normalPadding
import com.zen.accounts.presentation.ui.theme.normalTextSize
import com.zen.accounts.presentation.ui.theme.red_color
import com.zen.accounts.presentation.ui.theme.roundedCornerShape

data object CustomKeyboardOptions {
    val default = KeyboardOptions.Default
    val textEditor = KeyboardOptions.Default.copy(
        capitalization = KeyboardCapitalization.Sentences,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    )
    val lastTextEditor = KeyboardOptions.Default.copy(
        capitalization = KeyboardCapitalization.Sentences,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    )
    val numberEditor = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Phone,
        imeAction = ImeAction.Next
    )
    val emailEditor = KeyboardOptions.Default.copy(
        capitalization = KeyboardCapitalization.None,
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next
    )
    val passwordEditor = KeyboardOptions.Default.copy(
        capitalization = KeyboardCapitalization.None,
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done
    )
}

@Composable
fun GeneralEditText(
    text: String,
    modifier: Modifier,
    onValueChange: (String) -> Unit = {},
    placeholderText: String = "Enter Text",
    required: Boolean = false,
    showRequiredText : Boolean = false,
    singleLine: Boolean = true,
    enable: Boolean = true,
    error: Pair<Boolean, String> = Pair(false, ""),
    keyboardOptions: KeyboardOptions = CustomKeyboardOptions.default,
    clickableFun: (() -> Unit)? = null,
    showClickEffect: Boolean = true,
    @DrawableRes trailingIcon: Int? = null,
    trailingIconClick: (() -> Unit)? = null
) {

    @Composable
    fun InnerFunc() {
        val mutablePasswordVisible = rememberSaveable {
            mutableStateOf(keyboardOptions.keyboardType != KeyboardType.Password)
        }
        val passwordVisible = remember { derivedStateOf { mutablePasswordVisible.value } }
        val customSelectionColor = TextSelectionColors(
            handleColor = MaterialTheme.colors.onBackground,
            backgroundColor = MaterialTheme.colors.onBackground.copy(alpha = 0.4f)
        )
        CompositionLocalProvider(LocalTextSelectionColors provides customSelectionColor) {
            BasicTextField(
                value = text,
                onValueChange = onValueChange,
                modifier = modifier
                    .padding(horizontal = generalPadding, vertical = halfGeneralPadding)
                    .clip(RoundedCornerShape(generalPadding))
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = if (showClickEffect) LocalIndication.current else null
                    ) {
                        if (clickableFun != null) {
                            clickableFun()
                        }
                    }
                    .padding(generalPadding),
                singleLine = singleLine,
                cursorBrush = Brush.linearGradient(
                    listOf(
                        androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    )
                ),
                textStyle = TextStyle.Default.copy(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily(Font(R.font.montserrat)),
                    fontSize = normalTextSize
                ),
                enabled = enable,
                keyboardOptions = keyboardOptions,
                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation()
            ) {
                it()
                Row(
                    Modifier
                ) {
                    if (text.isEmpty()) {
                        if (required) {
                            Text(
                                text = "*",
                                style = Typography.bodySmall.copy(color = Color.Red)
                            )
                        }
                        Text(
                            text = placeholderText,
                            style = TextStyle.Default.copy(
                                color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceDim,
                                fontFamily = FontFamily(Font(R.font.montserrat)),
                                fontSize = normalTextSize
                            )
                        )
                    }

                    if (keyboardOptions.keyboardType == KeyboardType.Password) {
                        val passIcon =
                            if (passwordVisible.value) R.drawable.ic_eye else R.drawable.ic_eye_off
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(passIcon),
                                contentDescription = "eye_icon",
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .clickable {
                                        mutablePasswordVisible.value = !mutablePasswordVisible.value
                                    },
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (trailingIcon != null) {
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(trailingIcon),
                                contentDescription = "eye_icon",
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .clickable(
                                        interactionSource = remember {
                                            MutableInteractionSource()
                                        },
                                        indication = null
                                    ) {
                                        if (trailingIconClick != null) {
                                            trailingIconClick()
                                        }
                                    },
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

            }
        }
    }

    Column {
        if (required) {

            AnimatedVisibility(visible = showRequiredText) {
                Row(
                    modifier = Modifier.padding(start = generalPadding)
                ) {
                    Text(
                        text = "*",
                        style = Typography.bodyMedium.copy(color = Color.Red)
                    )

                    Text(
                        text = required_field,
                        style = Typography.bodySmall.copy(color = Color.Red)
                    )
                }
            }

            InnerFunc()

        } else {
            InnerFunc()
        }

        AnimatedVisibility(visible = error.first) {
            Text(
                text = error.second,
                style = Typography.bodySmall.copy(color = Color.Red),
                modifier = Modifier.padding(start = generalPadding)
            )
        }
    }

}

@Composable
fun GeneralButton(
    text: String,
    modifier: Modifier? = null,
    containerColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
    disabledContainerColor: Color = disabled_color,
    enable: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            ?: Modifier
                .fillMaxWidth()
                .padding(horizontal = generalPadding, vertical = halfGeneralPadding),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = disabledContainerColor
        ),
        enabled = enable,
        shape = RoundedCornerShape(generalPadding),
        onClick = { onClick() }
    ) {
        Text(
            text = text,
            style = Typography.bodyMedium.copy(color = Color.White),
            modifier = Modifier.padding(vertical = normalPadding)
        )
    }
}


@Composable
fun GeneralDropDown(
    modifier: Modifier,
    value: MutableState<BackupPlan>,
    showDropDown: MutableState<Boolean>,
    enable: Boolean = false,
    valueList: List<BackupPlan>,
    placeholderText: String = "Select Expense Type",
    onClick: () -> Unit = {},
    onItemClick: (BackupPlan) -> Unit
) {
    val localDensity = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val dropDownHeight: MutableState<Dp> = remember { mutableStateOf(0.dp) }
    val dropDownEdittextHeight: MutableState<Dp> = remember { mutableStateOf(0.dp) }
    val dropDownRowWidth = rememberSaveable(saver = dpSaver) { mutableStateOf(0.dp) }
    val dropDownIcon = rememberSaveable { mutableIntStateOf(R.drawable.ic_drop_down) }
    val text = remember {
        derivedStateOf { value.value.label }
    }

    if (!enable) {
        value.value = BackupPlan.Off
    }

    LaunchedEffect(key1 = showDropDown.value) {
        if (showDropDown.value) {
            dropDownIcon.intValue = R.drawable.ic_drop_up
        } else {
            dropDownIcon.intValue = R.drawable.ic_drop_down
        }
    }

    Row(
        modifier = modifier
    ) {
        GeneralEditText(
            text = text.value,
            modifier = Modifier
                .onGloballyPositioned {
                    dropDownRowWidth.value =
                        with(localDensity) { it.size.width.toDp() }
                    dropDownEdittextHeight.value =
                        with(localDensity) { it.size.height.toDp() }
                },
            enable = false,
            placeholderText = placeholderText,
            clickableFun = {
                if (enable) {
                    showDropDown.value = !showDropDown.value
                }
                onClick()
            },
            trailingIcon = dropDownIcon.intValue,
            trailingIconClick = {
                if (enable)
                    showDropDown.value = !showDropDown.value
                onClick()
            }
        )

        DropdownMenu(
            expanded = showDropDown.value,
            onDismissRequest = {
                showDropDown.value = false
            },
            modifier = Modifier
                .size(
                    width = dropDownRowWidth.value - generalPadding.times(2),
                    height = dropDownHeight.value
                )
                .background(androidx.compose.material3.MaterialTheme.colorScheme.surface),
            offset =
            if (screenWidth <= 500) DpOffset(
                dropDownRowWidth.value / 2 - (dropDownRowWidth.value - generalPadding.times(
                    2
                )) / 2, (-4).dp
            )
            else DpOffset(-dropDownRowWidth.value + generalPadding.times(2), 0.dp)
        ) {
            for (i in valueList.indices) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = valueList[i].label,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = Typography.bodyMedium.copy(color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
                        )
                    },
                    onClick = {
                        onItemClick(valueList[i])
                        value.value = valueList[i]
                        showDropDown.value = false
                    },
                    modifier = Modifier
                        .onGloballyPositioned {
                            dropDownHeight.value =
                                with(localDensity) { it.size.height.toDp() } * if (valueList.size < 6) valueList.size else 5
                        }
                )
            }
        }
    }
}

@Composable
fun GeneralSnackBar(
    visible: MutableState<Boolean>,
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    contentColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var textColor = contentColor
    if (containerColor == green_color || containerColor == red_color) {
        textColor = Color.White
    }
    AnimatedVisibility(
        modifier = modifier
            .zIndex(100f)
            .padding(horizontal = if (screenWidth <= 500) generalPadding else 150.dp)
            .padding(generalPadding)
            .clip(roundedCornerShape),
        visible = visible.value,
        enter = slideInVertically(initialOffsetY = { -300 }) + fadeIn(),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -300 })
    ) {
        Column(
            modifier = Modifier
                .background(containerColor)
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(generalPadding),
                style = Typography.bodyMedium.copy(color = textColor)
            )
        }
    }
}

@Composable
fun GeneralSnackBar(
    visible: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    contentColor: Color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var textColor = contentColor
    if (containerColor == green_color || containerColor == red_color) {
        textColor = Color.White
    }
    AnimatedVisibility(
        modifier = modifier
            .zIndex(100f)
            .padding(horizontal = if (screenWidth <= 500) generalPadding else 150.dp)
            .padding(generalPadding)
            .clip(roundedCornerShape),
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -300 }) + fadeIn(),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -300 })
    ) {
        Column(
            modifier = Modifier
                .background(containerColor)
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(generalPadding),
                style = Typography.bodyMedium.copy(color = textColor)
            )
        }
    }
}

