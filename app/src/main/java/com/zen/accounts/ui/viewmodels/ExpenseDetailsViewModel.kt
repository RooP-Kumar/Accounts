package com.zen.accounts.ui.viewmodels


import com.zen.accounts.db.model.Expense
import com.zen.accounts.db.model.ExpenseItem
import com.zen.accounts.repository.ExpenseRepository
import com.zen.accounts.ui.screens.main.expense_detail.ExpenseDetailUIState
import com.zen.accounts.ui.screens.main.expense_detail.ExpenseDetailUIStateExpense
import com.zen.accounts.ui.screens.main.expense_detail.ExpenseDetailUIStateExpenseItems
import com.zen.accounts.ui.screens.main.expense_detail.ExpenseDetailUIStateShowDeleteDialog
import com.zen.accounts.ui.screens.main.expense_detail.ExpenseDetailUIStateShowEditDialog
import com.zen.accounts.utility.io
import com.zen.accounts.utility.toExpense
import com.zen.accounts.utility.toExpenseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ExpenseDetailsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : BaseViewmodel() {

    private val _expenseDetailsUiStateHolder = MutableStateFlow(ExpenseDetailUIState())
    val expenseDetailsUiStateHolder = _expenseDetailsUiStateHolder.asStateFlow()

    private fun updateExpenseDetailsUiStateFlow(
        expense : Expense? = null,
        expenseItems : ArrayList<ExpenseItem>? = null,
        showEditDialog : Boolean? = null,
        showDeleteDialog : Boolean? = null,
    ) {
        val temp = expenseDetailsUiStateHolder.value.copy()
        expense?.let { temp.expense = it }
        expenseItems?.let { temp.expenseItems = it }
        showEditDialog?.let { temp.showEditDialog = it }
        showDeleteDialog?.let { temp.showDeleteDialog = it }
        _expenseDetailsUiStateHolder.update { temp }
    }

    fun updateExpense(updatedExpenseItem : ExpenseItem?, itemInd: Int) {
        io {
            val tempExpense = expenseDetailsUiStateHolder.value.copy()
            if(updatedExpenseItem != null) {
                tempExpense.expense = tempExpense.expense.copy(
                    totalAmount = tempExpense.expense.totalAmount -
                            tempExpense.expenseItems[itemInd].itemAmount!!.toDouble() +
                            (updatedExpenseItem.itemAmount ?: 0).toDouble()
                )

                tempExpense.expenseItems[itemInd] = updatedExpenseItem
                updateExpenseDetailsUiState(
                    tempExpense.expenseItems,
                    ExpenseDetailUIStateExpenseItems
                )

                tempExpense.expense.items[itemInd] = updatedExpenseItem
                updateExpenseDetailsUiState(
                    tempExpense.expense,
                    ExpenseDetailUIStateExpense
                )
            }
            expenseRepository.updateExpense(tempExpense.expense)
        }
    }

    fun deleteExpense() {
        io {
            expenseRepository.deleteExpenses(listOf(expenseDetailsUiStateHolder.value.expense.id))
        }
    }

    fun deleteExpenseItem(itemInd: Int) {
        io {
            val tempExpense = expenseDetailsUiStateHolder.value.copy()
            tempExpense.expense = tempExpense.expense.copy(totalAmount = tempExpense.expense.totalAmount -
                    tempExpense.expenseItems[itemInd].itemAmount!!.toDouble())

            tempExpense.expenseItems.removeAt(itemInd)
            updateExpenseDetailsUiState(
                tempExpense.expenseItems,
                ExpenseDetailUIStateExpenseItems
            )

            tempExpense.expense.items.removeAt(itemInd)
            updateExpenseDetailsUiState(
                tempExpense.expense,
                ExpenseDetailUIStateExpense
            )

            expenseRepository.updateExpense(tempExpense.expense)

            updateExpenseDetailsUiState(
                false,
                ExpenseDetailUIStateShowDeleteDialog
            )
        }
    }
    // <--------------------------------- Ui Updates Starts ---------------------------------->

    fun updateExpenseDetailsUiState(newValue : Any, fieldName: String) {
        when (fieldName) {
            ExpenseDetailUIStateExpense -> {updateExpenseDetailsUiStateFlow(expense = newValue.toExpense())}
            ExpenseDetailUIStateExpenseItems-> { updateExpenseDetailsUiStateFlow(expenseItems = newValue.toExpenseItem()) }
            ExpenseDetailUIStateShowEditDialog-> {updateExpenseDetailsUiStateFlow(showEditDialog = newValue.toString().toBoolean())}
            ExpenseDetailUIStateShowDeleteDialog-> {updateExpenseDetailsUiStateFlow(showDeleteDialog = newValue.toString().toBoolean())}
        }
    }

    // <--------------------------------- Ui Updates Ends ---------------------------------->
}