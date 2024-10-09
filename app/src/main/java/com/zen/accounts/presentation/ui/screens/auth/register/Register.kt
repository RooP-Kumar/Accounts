package com.zen.accounts.presentation.ui.screens.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.ui.navigation.Screen
import com.zen.accounts.presentation.ui.screens.common.CustomKeyboardOptions
import com.zen.accounts.presentation.ui.screens.common.GeneralButton
import com.zen.accounts.presentation.ui.screens.common.GeneralEditText
import com.zen.accounts.presentation.ui.screens.common.GeneralSnackBar
import com.zen.accounts.presentation.ui.screens.common.LoadingDialog
import com.zen.accounts.presentation.ui.screens.common.TopAppBar
import com.zen.accounts.presentation.ui.screens.common.already_have_account
import com.zen.accounts.presentation.ui.screens.common.enter_email
import com.zen.accounts.presentation.ui.screens.common.enter_name
import com.zen.accounts.presentation.ui.screens.common.enter_pass
import com.zen.accounts.presentation.ui.screens.common.enter_phone
import com.zen.accounts.presentation.ui.screens.common.invalid_email
import com.zen.accounts.presentation.ui.screens.common.invalid_pass
import com.zen.accounts.presentation.ui.screens.common.login_button_label
import com.zen.accounts.presentation.ui.screens.common.register_button_label
import com.zen.accounts.presentation.ui.screens.common.required_field
import com.zen.accounts.presentation.ui.theme.Typography
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.normalPadding
import com.zen.accounts.presentation.ui.theme.primary_color
import com.zen.accounts.presentation.ui.theme.topBarHeight
import com.zen.accounts.presentation.ui.viewmodels.RegisterScreenViewModel

@Composable
fun Register(
    appState: AppState,
    viewModel: RegisterScreenViewModel,
    currentScreen: Screen?,
    navigateUp: () -> Boolean
) {

    val registerUiStateHolder = viewModel.registerUiStateHolder.collectAsState()

    LaunchedEffect(key1 = registerUiStateHolder.value.loadingState) {
        viewModel.showSnackBarAccordingToLoadingState()
    }
    MainUI(appState = appState, viewModel, registerUiStateHolder, currentScreen, navigateUp)
}

@Composable
private fun MainUI(
    appState: AppState,
    viewModel: RegisterScreenViewModel,
    registerUiStateHolder: State<RegisterUiStateHolder>,
    currentScreen: Screen?,
    navigateUp : () -> Boolean
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    LoadingDialog(loadingState = registerUiStateHolder.value.loadingState)
    val mainAppUIStateHolder = viewModel.commonUIStateHolder.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            drawerState = appState.drawerState,
            navigateUp = navigateUp,
            currentScreen = currentScreen
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topBarHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = if (screenWidth > 500)
                    Modifier
                        .width(500.dp)
                        .verticalScroll(rememberScrollState())
                else
                    Modifier
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = generalPadding),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "* ",
                        style = Typography.bodySmall.copy(color = Color.Red)
                    )

                    Text(
                        text = required_field,
                        style = Typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                }

                GeneralEditText(
                    text = registerUiStateHolder.value.name,
                    onValueChange = {
                        viewModel.onTextFieldValueChange(it, registerUiStateHolder_name)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholderText = enter_name,
                    keyboardOptions = CustomKeyboardOptions.textEditor
                )

                GeneralEditText(
                    text = registerUiStateHolder.value.email,
                    required = true,
                    showRequiredText = registerUiStateHolder.value.emailRequired,
                    error = Pair(registerUiStateHolder.value.showEmailError, invalid_email),
                    onValueChange = {
                        viewModel.onTextFieldValueChange(it, registerUiStateHolder_email, true)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholderText = enter_email,
                    keyboardOptions = CustomKeyboardOptions.emailEditor
                )

                GeneralEditText(
                    text = registerUiStateHolder.value.phone,
                    onValueChange = {
                        viewModel.onTextFieldValueChange(it, registerUiStateHolder_phone)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholderText = enter_phone,
                    keyboardOptions = CustomKeyboardOptions.numberEditor
                )

                GeneralEditText(
                    text = registerUiStateHolder.value.password,
                    required = true,
                    showRequiredText = registerUiStateHolder.value.passRequired,
                    error = Pair(registerUiStateHolder.value.showPassError, invalid_pass),
                    onValueChange = {
                        viewModel.onTextFieldValueChange(it, registerUiStateHolder_password, true)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholderText = enter_pass,
                    keyboardOptions = CustomKeyboardOptions.passwordEditor
                )

                Row(
                    modifier = Modifier
                        .padding(horizontal = generalPadding, vertical = normalPadding)
                ) {
                    Text(
                        text = already_have_account,
                        style = Typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                    Spacer(modifier = Modifier.width(normalPadding))
                    Text(
                        text = login_button_label,
                        style = Typography.bodySmall.copy(color = primary_color),
                        modifier = Modifier
                            .clickable {
                                appState.navController.popBackStack()
                            }
                    )
                }

                GeneralButton(
                    text = register_button_label
                ) {

                    viewModel.registerUser()

                }
            }
        }

        GeneralSnackBar(
            visible = mainAppUIStateHolder.value.showSnackBar,
            text = mainAppUIStateHolder.value.snackBarText,
            modifier = Modifier.align(Alignment.TopCenter)
        )

    }
}