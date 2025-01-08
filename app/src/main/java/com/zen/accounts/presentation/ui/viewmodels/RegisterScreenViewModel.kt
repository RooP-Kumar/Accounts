package com.zen.accounts.presentation.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.data.db.model.User
import com.zen.accounts.domain.repository.AuthRepository
import com.zen.accounts.presentation.ui.screens.auth.register.RegisterUiStateHolder
import com.zen.accounts.presentation.ui.screens.auth.register.registerUiStateHolder_email
import com.zen.accounts.presentation.ui.screens.auth.register.registerUiStateHolder_emailRequired
import com.zen.accounts.presentation.ui.screens.auth.register.registerUiStateHolder_passRequired
import com.zen.accounts.presentation.ui.screens.auth.register.registerUiStateHolder_password
import com.zen.accounts.presentation.ui.screens.auth.register.registerUiStateHolder_showEmailError
import com.zen.accounts.presentation.ui.screens.auth.register.registerUiStateHolder_showPassError
import com.zen.accounts.presentation.ui.screens.common.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KMutableProperty1

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStore : UserDataStore
) : BaseViewmodel() {

    private val _registerUiStateHolder: MutableStateFlow<RegisterUiStateHolder> =
        MutableStateFlow(RegisterUiStateHolder())
    val registerUiStateHolder: StateFlow<RegisterUiStateHolder> =
        _registerUiStateHolder.asStateFlow()

    // update the uiStateHolder values
    private fun updateState(
        vararg strArgs: Pair<String, String> = arrayOf(),
        newLoadingState: LoadingState? = null
    ) {
        val temp = registerUiStateHolder.value.copy()
        val params = temp.getStringParamNames()

        newLoadingState?.let { temp.loadingState = it }

        for ((key, value) in strArgs) {
            params[key]?.set(temp, value)
        }
        _registerUiStateHolder.update { temp }
    }

    private fun updateState(
        vararg args : Pair<String, Boolean> = arrayOf()
    ) {
        val temp = registerUiStateHolder.value.copy()
        val params = temp::class.members
            .filterIsInstance<KMutableProperty1<RegisterUiStateHolder, Boolean>>()
            .associateBy { it.name }

        for ((key, value) in args) {
            params[key]?.set(temp, value)
        }
        _registerUiStateHolder.update { temp }
    }

    // <---------------------------- Business Logic Starts----------------------->
    fun registerUser() {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signUpUsingRetrofit(
                User(
                    name = _registerUiStateHolder.value.name,
                    phone = _registerUiStateHolder.value.phone,
                    email =  _registerUiStateHolder.value.email,
                    password =  _registerUiStateHolder.value.password
                )
            )
        }
        
//        val emailRegex =
//            """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".toRegex()
//        val passwordRegex = """^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{6,}$""".toRegex()
//        io {
//            val email = registerUiStateHolder.value.email
//            val pass = registerUiStateHolder.value.password
//
//            if (email.trim().isEmpty()) {
//                updateState(registerUiStateHolder_emailRequired to true)
//            } else if (pass.trim().isEmpty()) {
//                updateState(registerUiStateHolder_passRequired to true)
//            } else if (!registerUiStateHolder.value.phone.isDigitsOnly() || (registerUiStateHolder.value.phone.isNotEmpty() && registerUiStateHolder.value.phone.length != 10)) {
//                updateCommonUiState(true, "Phone number should contains only 10 numbers or empty.")
//            } else {
//                if (!emailRegex.matches(email)) {
//                    updateState(registerUiStateHolder_showEmailError to true)
//                } else if (!passwordRegex.matches(pass)) {
//                    updateState(registerUiStateHolder_showPassError to true)
//                } else {
//                    val newUser = User(
//                        name = registerUiStateHolder.value.name,
//                        email = email,
//                        phone = registerUiStateHolder.value.phone
//                    )
//
//                    updateState(newLoadingState = LoadingState.LOADING)
//                    when (
//                        val res = authRepository.registerUser(
//                            newUser,
//                            pass
//                        )
//                    ) {
//                        is Resource.SUCCESS -> {
//                            dataStore.saveUser(user = newUser)
//                            updateState(newLoadingState = LoadingState.SUCCESS)
//                            updateCommonUiState(snackBarText = success_register)
//                            updateState(
//                                strArgs = arrayOf(
//                                    "email" to "",
//                                    "name" to "",
//                                    "phone" to "",
//                                    "password" to ""
//                                )
//                            )
//                        }
//
//                        is Resource.FAILURE -> {
//                            updateState(newLoadingState = LoadingState.FAILURE)
//                            updateCommonUiState(snackBarText = res.message)
//                            updateState(
//                                strArgs = arrayOf(
//                                    "email" to "",
//                                    "name" to "",
//                                    "phone" to "",
//                                    "password" to ""
//                                )
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }
    // <---------------------------- Business Logic End----------------------->

    // <---------------------------- Handle UI Updates Starts----------------------->

    fun showSnackBarAccordingToLoadingState() {
        when (registerUiStateHolder.value.loadingState) {
            LoadingState.IDLE -> {}
            LoadingState.LOADING -> {}
            LoadingState.SUCCESS -> {
                showSnackBar()
            }

            LoadingState.FAILURE -> {
                showSnackBar()
            }
        }
    }

    fun onTextFieldValueChange(newText: String, fieldName: String, requiredField: Boolean = false) {
        if (newText.isNotEmpty() && requiredField) {
            when (fieldName) {
                registerUiStateHolder_email -> {
                    updateState(registerUiStateHolder_emailRequired to false)
                    updateState(registerUiStateHolder_showEmailError to false)
                }

                registerUiStateHolder_password -> {
                    updateState(registerUiStateHolder_passRequired to false)
                    updateState(registerUiStateHolder_showPassError to false)
                }
            }
        }
        updateState(strArgs = arrayOf(fieldName to newText))
    }

    // <---------------------------- Handle UI Updates End----------------------->
}
