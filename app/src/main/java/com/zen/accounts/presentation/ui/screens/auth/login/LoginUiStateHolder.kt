package com.zen.accounts.presentation.ui.screens.auth.login

import com.zen.accounts.presentation.ui.screens.common.LoadingState
import kotlin.reflect.KMutableProperty1

data class LoginUiStateHolder(
    var emailUsernamePhone: String = "",
    var password: String = "",
    var loadingState: LoadingState = LoadingState.IDLE,
    var emailError: Boolean = false,
    var passError: Boolean = false
) {
    fun getStringParamNames() : Map<String, KMutableProperty1<LoginUiStateHolder, String>> {
        return this::class.members
            .filterIsInstance<KMutableProperty1<LoginUiStateHolder, String>>()
            .associateBy { it.name }
    }
    fun getBoolParamNames() : Map<String, KMutableProperty1<LoginUiStateHolder, Boolean>> {
        return this::class.members
            .filterIsInstance<KMutableProperty1<LoginUiStateHolder, Boolean>>()
            .associateBy { it.name }
    }
}

const val loginUiStateHolder_showEmailError = "emailError"
const val loginUiStateHolder_showPassError = "passError"
const val loginUiStateHolder_emailOrPhone = "emailUsernamePhone"
const val loginUiStateHolder_pass = "password"
