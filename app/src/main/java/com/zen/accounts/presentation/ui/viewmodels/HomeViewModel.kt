package com.zen.accounts.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.zen.accounts.data.repositoryImpl.ExpenseRepository
import com.zen.accounts.presentation.ui.screens.main.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    val allExpense get() = expenseRepository.allExpense
    val monthlyExpense get() = expenseRepository.monthlyExpense

    val homeUiState by lazy {
        HomeUiState()
    }

}