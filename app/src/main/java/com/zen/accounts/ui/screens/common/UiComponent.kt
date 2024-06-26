package com.zen.accounts.ui.screens.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zen.accounts.R
import com.zen.accounts.states.AppState
import com.zen.accounts.ui.navigation.getScreenRouteWithTitle
import com.zen.accounts.ui.theme.Typography
import com.zen.accounts.ui.theme.background
import com.zen.accounts.ui.theme.generalPadding
import com.zen.accounts.ui.theme.halfGeneralPadding
import com.zen.accounts.ui.theme.onBackground
import com.zen.accounts.ui.theme.onSurface
import com.zen.accounts.ui.theme.topBarHeight
import com.zen.accounts.utility.generalBorder
import kotlinx.coroutines.launch

@Composable
fun LoadingDialog(
    loadingState: MutableState<LoadingState>,
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {}
) {
    val showDialog: MutableState<Boolean> = remember { mutableStateOf(false) }
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
                    .background(background)
                    .padding(generalPadding)
                ,
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
                    style = Typography.bodyMedium.copy(color = onSurface)
                )
            }
        }
    }

}

@Composable
fun TopBarBackButton(appState: AppState) {
    val coroutineScope = rememberCoroutineScope()
    Icon(
        painter = painterResource(id = R.drawable.ic_back),
        "back button",
        modifier = Modifier
            .padding(start = generalPadding)
            .clip(RoundedCornerShape(generalPadding))
            .clickable {
                appState.navController.popBackStack()
            }
            .padding(halfGeneralPadding),
        tint = onBackground
    )
}

@Composable
fun TopAppBar(
    appState: AppState,
    buttonEnableCondition: Boolean = false,
    btnText : String = "Done",
    painterResource : Painter? = null,
    onClick: (() -> Unit)? = null
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    if(screenWidth <= 500.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(topBarHeight)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TopBarBackButton(appState = appState)

            Text(
                text = getScreenRouteWithTitle().find { it.route == appState.navController.currentDestination?.route }?.title
                    ?: "",
                style = Typography.bodyLarge.copy(onBackground),
                modifier = Modifier
                    .padding(generalPadding)
                    .weight(1f)
            )

            if (onClick != null && painterResource != null) {
                IconButton(onClick = { onClick() }) {
                    Icon(
                        painter = painterResource,
                        contentDescription ="icon description",
                        tint = onBackground
                    )
                }

            } else if(onClick != null){
                GeneralButton(
                    text = btnText,
                    modifier = Modifier.padding(horizontal = generalPadding),
                    enable = buttonEnableCondition
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
                .height(topBarHeight)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if(appState.drawerState.value != null) {
                    if(appState.drawerState.value!!.isClosed)
                        coroutineScope.launch {
                            appState.drawerState.value!!.open()
                        }
                    else
                        coroutineScope.launch {
                            appState.drawerState.value!!.open()
                        }
                }
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = "menu icon"
                )
            }

            Text(
                text = getScreenRouteWithTitle().find { it.route == appState.navController.currentDestination?.route }?.title
                    ?: "",
                style = Typography.bodyLarge.copy(onBackground),
                modifier = Modifier
                    .padding(generalPadding)
                    .weight(1f)
            )

            if (onClick != null && painterResource != null) {
                IconButton(onClick = { onClick() }) {
                    Icon(
                        painter = painterResource,
                        contentDescription ="icon description",
                        tint = onBackground
                    )
                }

            } else if(onClick != null){
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
    dialogProperties: DialogProperties = DialogProperties(),
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
                    .background(background)
            ) {
                content.invoke(this)
            }
        }
    }
}

