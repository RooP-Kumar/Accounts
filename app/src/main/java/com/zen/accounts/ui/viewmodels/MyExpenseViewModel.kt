package com.zen.accounts.ui.viewmodels

//import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolder
import android.util.Log
import com.zen.accounts.db.dao.ExpenseWithOperation
import com.zen.accounts.repository.ExpenseItemRepository
import com.zen.accounts.repository.ExpenseRepository
import com.zen.accounts.ui.screens.common.LoadingState
import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolder
import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolderCheckBoxList
import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolderLoadingState
import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolderSelectAll
import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolderShowDeleteDialog
import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolderShowExpenseList
import com.zen.accounts.ui.screens.main.myexpense.MyExpenseUiStateHolderShowSelectCheckBox
import com.zen.accounts.utility.io
import com.zen.accounts.utility.toHashMap
import com.zen.accounts.utility.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val TAG = "MyExpenseViewModel"

@HiltViewModel
class MyExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val expenseItemRepository: ExpenseItemRepository
) : BaseViewmodel() {
    var isMonthlyCalled = false
    private val _allExpense : MutableStateFlow<List<ExpenseWithOperation>> = MutableStateFlow(listOf())
    val allExpense: StateFlow<List<ExpenseWithOperation>> = _allExpense.asStateFlow()
    var monthlyExpense: List<ExpenseWithOperation> = listOf()

    fun getAllExpenses() {
        io {
            expenseRepository.allExpense.collectLatest { expense ->
                _allExpense.update { expense }
            }
            expenseRepository.monthlyExpense.collectLatest {
                monthlyExpense = it
            }
        }
    }

    private val _myExpenseUiStateFlow = MutableStateFlow(MyExpenseUiStateHolder())
    val myExpenseUiStateFlow = _myExpenseUiStateFlow.asStateFlow()

    private fun updateMyExpenseUiStateFlow(newState: MyExpenseUiStateHolder) {
        _myExpenseUiStateFlow.update { newState }
    }

    fun deleteExpenses(expenses: List<Long>) {
        io {
            updateMyExpenseUiState(LoadingState.LOADING, MyExpenseUiStateHolderLoadingState)
            updateMyExpenseUiState(hashMapOf<Long, String>(), MyExpenseUiStateHolderCheckBoxList)
            updateMyExpenseUiState(false, MyExpenseUiStateHolderShowSelectCheckBox)
            updateMyExpenseUiState(false, MyExpenseUiStateHolderSelectAll)
            expenseRepository.deleteExpenses(expenses)
            delay(500)
            updateMyExpenseUiState(LoadingState.SUCCESS, MyExpenseUiStateHolderLoadingState)
        }
    }

    fun updateMyExpenseUiState(newValue: Any, fieldName: String) {
        io {
            val temp = myExpenseUiStateFlow.value.copy()
            when (fieldName) {
                MyExpenseUiStateHolderShowSelectCheckBox -> {
                    temp.showSelectCheckbox = newValue.toString().toBoolean()
                }

                MyExpenseUiStateHolderCheckBoxList -> {
                    temp.checkBoxMap = newValue.toHashMap()
                    if (!isMonthlyCalled)
                        temp.selectAll = newValue.toHashMap().size == allExpense.value.size
                    else
                        temp.selectAll = newValue.toHashMap().size == monthlyExpense.size
                }

                MyExpenseUiStateHolderSelectAll -> {
                    temp.selectAll = newValue.toString().toBoolean()
                }

                MyExpenseUiStateHolderLoadingState -> {
                    temp.loadingState = newValue.toLoadingState()
                }

                MyExpenseUiStateHolderShowDeleteDialog -> {
                    temp.showDeleteDialog = newValue.toString().toBoolean()
                }

                MyExpenseUiStateHolderShowExpenseList -> {
                    temp.showExpenseList = newValue.toString().toBoolean()
                }
            }
            updateMyExpenseUiStateFlow(temp)
        }
    }

}