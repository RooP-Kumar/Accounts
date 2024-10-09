package com.zen.accounts.presentation.ui.viewmodels

//import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolder
import com.zen.accounts.data.db.dao.ExpenseWithOperation
import com.zen.accounts.data.repositoryImpl.ExpenseItemRepository
import com.zen.accounts.data.repositoryImpl.ExpenseRepository
import com.zen.accounts.presentation.ui.screens.common.LoadingState
import com.zen.accounts.presentation.utility.io
import com.zen.accounts.presentation.utility.toHashMap
import com.zen.accounts.presentation.utility.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MyExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val expenseItemRepository: ExpenseItemRepository
) : BaseViewmodel() {
    var isMonthlyCalled = false
    var allExpense: List<ExpenseWithOperation> = listOf()
    var monthlyExpense: List<ExpenseWithOperation> = listOf()
    val expenses = expenseRepository.expenses
    init {
        io {
            expenseRepository.allExpense.collectLatest {
                allExpense = it
            }
            expenseRepository.monthlyExpense.collectLatest {
                allExpense = it
            }
        }
    }

    fun deleteExpenses(expenses: List<Long>) {
        io {
            expenseRepository.deleteExpenses(expenses)
            delay(500)
        }
    }
}
