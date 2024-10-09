package com.zen.accounts.presentation.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.zen.accounts.presentation.CommonUIStateHolder
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.utility.main
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
open class BaseViewmodel @Inject constructor() : ViewModel() {
    @Inject
    @ApplicationContext
    lateinit var context: Context
    @Inject
    lateinit var appState: AppState

    private val _commonUIStateHolder: MutableStateFlow<CommonUIStateHolder> =
        MutableStateFlow(CommonUIStateHolder())
    val commonUIStateHolder: StateFlow<CommonUIStateHolder> = _commonUIStateHolder.asStateFlow()

    fun updateCommonUiState(showSnackBar: Boolean? = null, snackBarText: String? = null) {
        val temp = commonUIStateHolder.value.copy()
        showSnackBar?.let { temp.showSnackBar = it }
        snackBarText?.let { temp.snackBarText = it }
        _commonUIStateHolder.update { temp }
    }

    fun showSnackBar() {
        main {
            updateCommonUiState(true)
            delay(2500)
            updateCommonUiState(false)
        }
    }
}