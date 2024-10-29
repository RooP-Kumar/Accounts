package com.zen.accounts.presentation.ui.viewmodels

import com.zen.accounts.data.db.dao.ExpenseWithOperation
import com.zen.accounts.data.repositoryImpl.ExpenseRepository
import com.zen.accounts.presentation.ui.screens.main.myexpense.MyExpenseState
import com.zen.accounts.presentation.ui.screens.main.myexpense.ScreenLoadingState
import com.zen.accounts.presentation.utility.io
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MyExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
) : BaseViewmodel() {
    var isMonthlyCalled = false
    var allExpense: Flow<List<ExpenseWithOperation>> = expenseRepository.allExpense
    var monthlyExpense: Flow<List<ExpenseWithOperation>> = expenseRepository.monthlyExpense
    
    private val _myExpenseUiState : MutableStateFlow<MyExpenseState> = MutableStateFlow(
        MyExpenseState(screenLoadingState = ScreenLoadingState.Success(listOf()))
    )
    val myExpenseUiState = _myExpenseUiState
    
    fun fetchList(isMonthly: Boolean) {
        io {
            _myExpenseUiState.update {
                it.copy(screenLoadingState = ScreenLoadingState.Loading)
            }
            if(!isMonthly) {
                expenseRepository.allExpense.collectLatest { list ->
                    delay(800)
                    _myExpenseUiState.update {
                        it.copy(screenLoadingState = ScreenLoadingState.Success(list))
                    }
                }
            } else {
                expenseRepository.monthlyExpense.collectLatest { list ->
                    delay(800)
                    _myExpenseUiState.update {
                        it.copy(screenLoadingState = ScreenLoadingState.Success(list))
                    }
                }
            }
        }
    }
    
    fun updateSelectAll(newValue: Boolean) {
        _myExpenseUiState.update { it.copy(selectAll = newValue) }
    }
    
    fun updateShowCheckBoxes(newValue : Boolean) {
        _myExpenseUiState.update { it.copy(showCheckBoxes = newValue) }
    }
    
    fun updateCheckBoxSet(newValue : Set<Long>) {
        _myExpenseUiState.update { it.copy(checkBoxSet = newValue) }
    }
    
    fun updateShowDeleteDialog(newValue : Boolean) {
        _myExpenseUiState.update { it.copy(showDeleteDialog = newValue) }
    }
    
    fun deleteExpenses(expenses: List<Long>) {
        io {
            val temp = myExpenseUiState.value.checkBoxSet.toMutableSet()
            expenses.forEach { temp.remove(it) }
            expenseRepository.deleteExpenses(expenses)
            _myExpenseUiState.update { it.copy(checkBoxSet = temp) }
            delay(500)
        }
    }
}
