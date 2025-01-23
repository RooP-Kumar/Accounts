package com.zen.accounts.presentation.ui.screens.common

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zen.accounts.R
import com.zen.accounts.presentation.ui.navigation.Screen
import com.zen.accounts.presentation.ui.theme.AccountsThemes
import com.zen.accounts.presentation.ui.theme.DarkBackground
import com.zen.accounts.presentation.ui.theme.Typography
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.halfGeneralPadding
import com.zen.accounts.presentation.ui.theme.primary_color
import com.zen.accounts.presentation.ui.theme.secondary_color
import com.zen.accounts.presentation.ui.theme.topBarHeight
import com.zen.accounts.presentation.utility.generalBorder
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.ceil

@Composable
fun LoadingDialog(
    loadingState : State<LoadingState>,
    onSuccess : () -> Unit = {},
    onFailure : () -> Unit = {}
) {
    val showDialog : MutableState<Boolean> = remember { mutableStateOf(false) }
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_anim))
    LaunchedEffect(key1 = loadingState.value) {
        when (loadingState.value) {
            LoadingState.LOADING -> {
                showDialog.value = true
            }
            
            LoadingState.SUCCESS -> {
                showDialog.value = false
                onSuccess()
            }
            
            LoadingState.FAILURE -> {
                showDialog.value = false
                onFailure()
            }
            
            else -> {
                showDialog.value = false
            }
        }
    }
    
    AnimatedVisibility(visible = showDialog.value) {
        Dialog(
            onDismissRequest = {}
        ) {
            Column(
                modifier = Modifier
                    .generalBorder()
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                    .padding(generalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    composition = lottieComposition,
                    contentScale = ContentScale.Fit,
                    restartOnPlay = true,
                    iterations = Int.MAX_VALUE,
                    modifier = Modifier
                        .size(50.dp, 50.dp)
                )
                Spacer(modifier = Modifier.height(halfGeneralPadding))
                Text(
                    text = "Loading.....",
                    style = Typography.bodyMedium.copy(color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
                )
            }
        }
    }
    
}

@Composable
fun LoadingDialog(
    loadingState : LoadingState,
    onSuccess : () -> Unit = {},
    onFailure : () -> Unit = {}
) {
    val showDialog : MutableState<Boolean> = remember { mutableStateOf(false) }
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_anim))
    LaunchedEffect(key1 = loadingState) {
        when (loadingState) {
            LoadingState.LOADING -> {
                showDialog.value = true
            }
            
            LoadingState.SUCCESS -> {
                showDialog.value = false
                onSuccess()
            }
            
            LoadingState.FAILURE -> {
                showDialog.value = false
                onFailure()
            }
            
            else -> {
                showDialog.value = false
            }
        }
    }
    
    AnimatedVisibility(visible = showDialog.value) {
        Dialog(
            onDismissRequest = {}
        ) {
            Column(
                modifier = Modifier
                    .generalBorder()
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                    .padding(generalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    composition = lottieComposition,
                    contentScale = ContentScale.Fit,
                    restartOnPlay = true,
                    iterations = Int.MAX_VALUE,
                    modifier = Modifier
                        .size(50.dp, 50.dp)
                )
                Spacer(modifier = Modifier.height(halfGeneralPadding))
                Text(
                    text = "Loading.....",
                    style = Typography.bodyMedium.copy(color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
                )
            }
        }
    }
    
}

@Composable
fun LoadingDialog(
    showDialog : Boolean
) {
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_anim))
    AnimatedVisibility(visible = showDialog) {
        Dialog(
            onDismissRequest = {}
        ) {
            Column(
                modifier = Modifier
                    .generalBorder()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(generalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    composition = lottieComposition,
                    contentScale = ContentScale.Fit,
                    restartOnPlay = true,
                    iterations = Int.MAX_VALUE,
                    modifier = Modifier
                        .size(50.dp, 50.dp)
                )
                Spacer(modifier = Modifier.height(halfGeneralPadding))
                Text(
                    text = "Loading.....",
                    style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                )
            }
        }
    }
    
}

@Composable
fun TopBarBackButton(
    navigateUp : () -> Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .padding(start = generalPadding)
            .size(30.dp)
            .clip(CircleShape)
            .clickable {
                navigateUp()
            }
            .then(
                if (isSystemInDarkTheme()) {
                    Modifier.border(0.5.dp, color = primary_color, shape = CircleShape)
                } else {
                    Modifier
                        .clip(shape = CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                }
            )
    
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            "back button",
            modifier = Modifier
                .align(Alignment.Center)
                .clip(shape = CircleShape)
                .then(
                    if (isSystemInDarkTheme()) {
                        Modifier.background(DarkBackground)
                    } else {
                        Modifier.background(secondary_color)
                    }
                )
                .padding(6.dp),
            tint = primary_color
        )
    }
}

@Composable
private fun ShowPrevBackButton() {
    AccountsThemes {
        TopBarBackButton { false }
    }
}

@Composable
fun TopAppBar(
    drawerState : MutableState<DrawerState?>? = null,
    buttonEnableCondition : Boolean = false,
    btnText : String = "Done",
    painterResource : Painter? = null,
    currentScreen : Screen? = null,
    navigateUp : () -> Boolean = { false },
    onClick : (() -> Unit)? = null
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    
    if (screenWidth <= 500.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(topBarHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            TopBarBackButton(navigateUp = navigateUp)
            
            Text(
                text = currentScreen?.title
                    ?: "",
                style = Typography.bodyLarge.copy(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(generalPadding)
                    .weight(1f)
            )
            
            if (onClick != null && painterResource != null) {
                IconButton(onClick = { onClick() }) {
                    Icon(
                        painter = painterResource,
                        contentDescription = "icon description",
                        tint = primary_color
                    )
                }
                
            } else if (onClick != null) {
                GeneralButton(
                    text = btnText,
                    modifier = Modifier.padding(horizontal = generalPadding),
                    enable = buttonEnableCondition,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    onClick()
                }
            }
        }
    } else {
        val coroutineScope = rememberCoroutineScope()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(topBarHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentScreen?.route !in listOf(
                    Screen.LoginScreen.route,
                    Screen.RegisterScreen.route,
                    Screen.Home.route,
                    Screen.SettingScreen.route
                )
            ) {
                TopBarBackButton(navigateUp)
            } else {
                IconButton(onClick = {
                    if (drawerState?.value != null) {
                        if (drawerState.value!!.isClosed)
                            coroutineScope.launch {
                                drawerState.value!!.open()
                            }
                        else
                            coroutineScope.launch {
                                drawerState.value!!.open()
                            }
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_menu),
                        contentDescription = "menu icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Text(
                text = currentScreen?.title
                    ?: "",
                style = Typography.bodyLarge.copy(androidx.compose.material3.MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(generalPadding)
                    .weight(1f)
            )
            
            if (onClick != null && painterResource != null) {
                IconButton(onClick = { onClick() }) {
                    Icon(
                        painter = painterResource,
                        contentDescription = "icon description",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    )
                }
                
            } else if (onClick != null) {
                GeneralButton(
                    text = btnText,
                    modifier = Modifier.padding(horizontal = generalPadding),
                    enable = buttonEnableCondition
                ) {
                    onClick()
                }
            }
        }
    }
    
}

@Composable
fun GeneralDialog(
    showDialog : MutableState<Boolean>,
    dialogProperties : DialogProperties = DialogProperties(),
    content : @Composable ColumnScope.() -> Unit
) {
    AnimatedVisibility(visible = showDialog.value) {
        Dialog(
            onDismissRequest = {
                showDialog.value = false
            },
            dialogProperties
        ) {
            Column(
                modifier = Modifier
                    .generalBorder()
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            ) {
                content.invoke(this)
            }
        }
    }
}

@Composable
fun GeneralDialog(
    showDialog : Boolean,
    dialogProperties : DialogProperties = DialogProperties(),
    onDismissRequest : () -> Unit = {},
    content : @Composable ColumnScope.() -> Unit
) {
    AnimatedVisibility(visible = showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            dialogProperties
        ) {
            Column(
                modifier = Modifier
                    .generalBorder()
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            ) {
                content.invoke(this)
            }
        }
    }
}
