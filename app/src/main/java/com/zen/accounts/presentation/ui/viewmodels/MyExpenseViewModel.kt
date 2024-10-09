package com.zen.accounts.presentation.ui.viewmodels

//import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolder
import com.zen.accounts.data.db.dao.ExpenseWithOperation
import com.zen.accounts.data.repositoryImpl.ExpenseRepository
import com.zen.accounts.presentation.utility.io
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MyExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
) : BaseViewmodel() {
    var isMonthlyCalled = false
    var allExpense: Flow<List<ExpenseWithOperation>> = expenseRepository.allExpense
    var monthlyExpense: Flow<List<ExpenseWithOperation>> = expenseRepository.monthlyExpense
    
    fun deleteExpenses(expenses: List<Long>) {
        io {
            expenseRepository.deleteExpenses(expenses)
            delay(500)
        }
    }
}
