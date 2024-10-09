package com.zen.accounts.presentation.ui.screens.auth.register

import com.zen.accounts.presentation.ui.screens.common.LoadingState
import kotlin.reflect.KMutableProperty1

data class RegisterUiStateHolder(
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var password: String = "",
    var snackBarText: String = "",
    var loadingState: LoadingState = LoadingState.IDLE,
    var showSnackBar: Boolean = false,
    var emailRequired: Boolean = false,
    var showEmailError: Boolean = false,
    var passRequired: Boolean = false,
    var showPassError: Boolean = false
) {
    fun getStringParamNames() : Map<String, KMutableProperty1<RegisterUiStateHolder, String>>{
        return this::class.members
            .filterIsInstance<KMutableProperty1<RegisterUiStateHolder, String>>()
            .associateBy { it.name }
    }

    fun getBoolParamNames() : Map<String, KMutableProperty1<RegisterUiStateHolder, Boolean>>{
        return this::class.members
            .filterIsInstance<KMutableProperty1<RegisterUiStateHolder, Boolean>>()
            .associateBy { it.name }
    }
}

// These value are the same as above ui state holder class properties name. so that we can update these value with these values.
const val registerUiStateHolder_name = "name"
const val registerUiStateHolder_email = "email"
const val registerUiStateHolder_phone = "phone"
const val registerUiStateHolder_password = "password"
const val registerUiStateHolder_emailRequired = "emailRequired"
const val registerUiStateHolder_showEmailError = "showEmailError"
const val registerUiStateHolder_passRequired = "passRequired"
const val registerUiStateHolder_showPassError = "showPassError"