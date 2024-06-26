package com.zen.accounts.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zen.accounts.api.resource.Resource
import com.zen.accounts.db.datastore.UserDataStore
import com.zen.accounts.db.model.User
import com.zen.accounts.repository.AuthRepository
import com.zen.accounts.ui.screens.auth.register.RegisterUiState
import com.zen.accounts.ui.screens.common.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    val registerUiState by lazy { RegisterUiState() }
    fun registerUser(user: User, pass : String, dataStore: UserDataStore) {
        viewModelScope.launch {
            registerUiState.apply {
                loadingState.value = LoadingState.LOADING
                when(val res = authRepository.registerUser(user, pass)) {
                    is Resource.SUCCESS -> {
                        loadingState.value = LoadingState.SUCCESS
                        snackBarText.value = "You can successfully login to your account."
                        userName.value = ""
                        email.value = ""
                        phone.value = ""
                        password.value = ""
                    }
                    is Resource.FAILURE -> {
                        snackBarText.value = res.message
                        loadingState.value = LoadingState.FAILURE
                        userName.value = ""
                        email.value = ""
                        phone.value = ""
                        password.value = ""
                    }
                }
            }
        }
    }
}