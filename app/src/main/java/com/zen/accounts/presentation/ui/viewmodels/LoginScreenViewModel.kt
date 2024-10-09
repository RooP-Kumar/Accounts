package com.zen.accounts.presentation.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.zen.accounts.data.api.resource.Resource
import com.zen.accounts.data.repositoryImpl.ExpenseRepository
import com.zen.accounts.domain.repository.AuthRepository
import com.zen.accounts.presentation.ui.screens.auth.login.LoginUiStateHolder
import com.zen.accounts.presentation.ui.screens.auth.login.loginUiStateHolder_pass
import com.zen.accounts.presentation.ui.screens.common.LoadingState
import com.zen.accounts.presentation.ui.screens.common.empty_email
import com.zen.accounts.presentation.ui.screens.common.empty_pass
import com.zen.accounts.presentation.ui.screens.auth.login.*
import com.zen.accounts.presentation.ui.screens.common.success_login
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository
) : BaseViewmodel() {

    private val _loginUiStateHolder = MutableStateFlow(LoginUiStateHolder())
    val loginUiStateHolder = _loginUiStateHolder.asStateFlow()

    private fun updateLoginUiStateHolder(
        vararg strArgs: Pair<String, String> = arrayOf(), loadingState: LoadingState? = null
    ) {
        val temp = loginUiStateHolder.value.copy()
        loadingState?.let { temp.loadingState = it }
        val params = temp.getStringParamNames()
        for ((key, value) in strArgs) {
            params[key]?.set(temp, value)
        }
        _loginUiStateHolder.update { temp }
    }

    private fun updateLoginUiStateHolder(
        vararg strArgs: Pair<String, Boolean> = arrayOf()
    ) {
        val temp = loginUiStateHolder.value.copy()
        val params = temp.getBoolParamNames()
        for ((key, value) in strArgs) {
            params[key]?.set(temp, value)
        }
        _loginUiStateHolder.update { temp }
    }

    // <------------------------ Business Logic Starts--------------------------------->
    private fun loginUser(email: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
                if (email.trim().isEmpty()) {
                    updateCommonUiState(snackBarText = empty_email)
                    showSnackBar()
                } else if (pass.trim().isEmpty()) {
                    updateCommonUiState(snackBarText = empty_pass)
                    showSnackBar()
                } else {
                    updateLoginUiStateHolder(loadingState = LoadingState.LOADING)
                    when (val res = authRepository.loginUser(email, pass)) {
                        is Resource.SUCCESS -> {
                            updateCommonUiState(snackBarText = success_login)
                            updateLoginUiStateHolder(loginUiStateHolder_emailOrPhone to "")
                            updateLoginUiStateHolder(loginUiStateHolder_pass to "")
                            expenseRepository.dataStore.run { saveUser(res.value.value) }
                            when (expenseRepository.getExpensesFromFirebase(res.value.value.uid)) {
                                is Resource.SUCCESS -> {
                                    updateLoginUiStateHolder(loadingState = LoadingState.SUCCESS)
                                }

                                is Resource.FAILURE -> {
                                    updateLoginUiStateHolder(loadingState = LoadingState.FAILURE)
                                }
                            }

                        }

                        is Resource.FAILURE -> {
                            updateCommonUiState(snackBarText = res.message)
                            updateLoginUiStateHolder(loginUiStateHolder_emailOrPhone to "")
                            updateLoginUiStateHolder(loginUiStateHolder_pass to "")
                            updateLoginUiStateHolder(loadingState = LoadingState.FAILURE)
                        }
                    }
                }
        }
    }

    fun loginUser() {
        val emailRegex =
            """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".toRegex()
        val passwordRegex = """^([a-zA-Z0-9._%+-]).{6,}$""".toRegex()
        val email = loginUiStateHolder.value.emailUsernamePhone.trim()
        val pass = loginUiStateHolder.value.password.trim()
        if (!emailRegex.matches(email)) {
            updateLoginUiStateHolder(loginUiStateHolder_showEmailError to true)
        } else if (!passwordRegex.matches(pass)) {
            updateLoginUiStateHolder(loginUiStateHolder_showPassError to true)
        } else {
            loginUser(email, pass)
        }
    }

    // <------------------------ Business Logic Ends--------------------------------->

    // <------------------------ UI Updates Starts--------------------------------->
    fun onTextFieldValueChange(newText : String, fieldName : String) {
        if(newText.isNotEmpty()) {
            when(fieldName) {
                loginUiStateHolder_emailOrPhone -> updateLoginUiStateHolder(loginUiStateHolder_showEmailError to false)
                loginUiStateHolder_pass -> updateLoginUiStateHolder(loginUiStateHolder_showPassError to false)
            }
        }
        updateLoginUiStateHolder(fieldName to newText)
    }
    // <------------------------ UI Updates Ends--------------------------------->
}