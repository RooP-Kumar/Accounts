package com.zen.accounts.presentation.ui.viewmodels

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewModelScope
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.data.db.model.ExpenseItem
import com.zen.accounts.data.repositoryImpl.ExpenseItemRepository
import com.zen.accounts.data.repositoryImpl.ExpenseRepository
import com.zen.accounts.presentation.ui.screens.common.LoadingState
import com.zen.accounts.presentation.ui.screens.main.add_expense_item.AddExpenseItemStateHolder
import com.zen.accounts.presentation.ui.screens.main.add_expense_item.addExpenseItemStateHolder_amount
import com.zen.accounts.presentation.ui.screens.main.add_expense_item.addExpenseItemStateHolder_confirmation_state
import com.zen.accounts.presentation.ui.screens.main.add_expense_item.addExpenseItemStateHolder_title
import com.zen.accounts.presentation.ui.screens.main.addexpense.AddExpenseUiStateHolder
import com.zen.accounts.presentation.ui.screens.main.addexpense.AddExpenseUiStateHolderAmount
import com.zen.accounts.presentation.ui.screens.main.addexpense.AddExpenseUiStateHolderLoadingState
import com.zen.accounts.presentation.ui.screens.main.addexpense.AddExpenseUiStateHolderTitle
import com.zen.accounts.presentation.ui.screens.main.addexpense.AddExpenseUiStateHolderWidth
import com.zen.accounts.presentation.utility.toArrayList
import com.zen.accounts.presentation.utility.toDp
import com.zen.accounts.presentation.utility.toLoadingState
import com.zen.accounts.data.repositoryImpl.worker_repository.WorkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val expenseItemRepository: ExpenseItemRepository,
    private val workerRepository: WorkerRepository
) : BaseViewmodel() {

    private val _addExpenseUiStateHolder = MutableStateFlow(AddExpenseUiStateHolder())
    val addExpenseUiStateHolder = _addExpenseUiStateHolder.asStateFlow()

    private val _addExpenseItemUiStateHolder = MutableStateFlow(AddExpenseItemStateHolder())
    val addExpenseItemUiStateHolder = _addExpenseItemUiStateHolder.asStateFlow()

    private fun updateAddExpenseUiStateValue(
        title: String? = null,
        amount: Double? = null,
        dropDownRowWidth: Dp? = null,
        loadingState: LoadingState? = null,
    ) {
        val temp = addExpenseUiStateHolder.value.copy()
        title?.let { temp.title = it }
        amount?.let { temp.totalExpenseAmount = it }
        dropDownRowWidth?.let { temp.expenseItemListAmountTextWidth = it }
        loadingState?.let { temp.loadingState = it }
        _addExpenseUiStateHolder.update { temp }
    }

    private fun updateAddExpenseItemUiStateHolder(
        title: String? = null,
        amount: String? = null,
        showAmountDropDown: Boolean? = null,
        dropDownRowWidth: Dp? = null,
        dropDownIcon: Int? = null,
        loadingState: LoadingState? = null,
        confirmationDialogState: Boolean? = null
    ) {
        val temp = _addExpenseItemUiStateHolder.value.copy()
        title?.let { temp.title = it }
        amount?.let { temp.amount = it }
        showAmountDropDown?.let { temp.showAmountDropDown = it }
        dropDownRowWidth?.let { temp.dropDownRowWidth = it }
        dropDownIcon?.let { temp.dropDownIcon = it }
        loadingState?.let { temp.loadingState = it }
        confirmationDialogState?.let { temp.confirmationDialogState = it }
        _addExpenseItemUiStateHolder.update { temp }
    }

    // <------------------------------ Business Logic Starts--------------------------------->
    val allExpenseItem: Flow<List<ExpenseItem>> = expenseItemRepository.allExpenseItem

    private fun addExpenseIntoLocalDatabase(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            updateAddExpenseUiStateValue(loadingState = LoadingState.LOADING)
            expenseRepository.insertExpenseIntoRoom(expense)
            delay(600)
            updateAddExpenseUiStateValue(loadingState = LoadingState.SUCCESS)

        }
    }

    private fun addExpenseItemIntoLocalDatabase(expenseItem: ExpenseItem) {
        viewModelScope.launch(Dispatchers.IO) {
            updateAddExpenseItemUiStateHolder(loadingState = LoadingState.LOADING)
            expenseItemRepository.insertExpenseItemIntoRoom(expenseItem)
            delay(600)
            updateAddExpenseItemUiStateHolder(loadingState = LoadingState.SUCCESS)
        }
    }

    fun deleteExpenseItemsFromLocalDatabase() {
        viewModelScope.launch {
            expenseItemRepository.clearExpenseItemTable()
        }
    }

    private fun addExpense(allExpenseItem: List<ExpenseItem>) {
        if(allExpenseItem.isEmpty()) {
            updateCommonUiState(snackBarText = "Please! add expense item.")
            showSnackBar()
        } else if(addExpenseUiStateHolder.value.title.isEmpty()) {
            updateCommonUiState(snackBarText = "Please! add title.")
            showSnackBar()
        } else {
            val tempExpense = Expense(
                id = System.currentTimeMillis(),
                title = addExpenseUiStateHolder.value.title,
                items = allExpenseItem.toArrayList(),
                totalAmount = addExpenseUiStateHolder.value.totalExpenseAmount,
                date = Date(System.currentTimeMillis())
            )
            addExpenseIntoLocalDatabase(tempExpense)
            deleteExpenseItemsFromLocalDatabase()
        }
    }

    // <------------------------------ Business Logic Ends--------------------------------->

    // <------------------------------ UI Updates Starts--------------------------------->

    fun updateStateValue(newValue : Any, fieldName: String) {
        when(fieldName) {
            addExpenseItemStateHolder_confirmation_state -> updateAddExpenseItemUiStateHolder(confirmationDialogState = newValue.toString().toBoolean())
            addExpenseItemStateHolder_title -> updateAddExpenseItemUiStateHolder(title = newValue.toString())
            addExpenseItemStateHolder_amount -> updateAddExpenseItemUiStateHolder(amount = newValue.toString())
        }
    }

    fun updateAddExpenseStateValue(newValue : Any, fieldName: String) {
        when(fieldName) {
            AddExpenseUiStateHolderTitle -> updateAddExpenseUiStateValue(title = newValue.toString())
            AddExpenseUiStateHolderAmount -> updateAddExpenseUiStateValue(amount = newValue.toString().toDouble())
            AddExpenseUiStateHolderLoadingState -> updateAddExpenseUiStateValue(loadingState = newValue.toLoadingState())
            AddExpenseUiStateHolderWidth -> updateAddExpenseUiStateValue(dropDownRowWidth = newValue.toDp())
        }
    }

    fun onAddExpenseItemClick() {
        try {
            val itemTitle = addExpenseItemUiStateHolder.value.title.trim()
            val itemPrice = addExpenseItemUiStateHolder.value.amount.trim().toDouble()

            if (itemTitle.isEmpty()) {
                updateCommonUiState(snackBarText = "Please enter title.")
                showSnackBar()
            } else {
                val expenseItem = ExpenseItem(System.currentTimeMillis(), itemTitle, itemPrice, Date())
                addExpenseItemIntoLocalDatabase(expenseItem)

            }
        } catch (e: Exception) {
            updateCommonUiState(snackBarText = "Amount should be number.")
            showSnackBar()
        }
    }

    fun onAddExpenseClick(allExpenseItems: List<ExpenseItem>) {
        addExpense(allExpenseItems)
    }

//    fun onEditDialogSaveClick(
//        title : String,
//        expenseItem: ExpenseItem
//    ) {
//        if(amount.trim().isEmpty()) {
//            context.toast("Amount can not be empty.")
//        } else {
//            updateAddExpenseStateValue(false, ExpenseDetailUIStateShowEditDialog)
//            if(isExpenseTitle) {
//                onSave(null, title.trim())
//            } else {
//                onSave(expenseItem.copy(itemTitle = title.trim(), itemAmount = amount.trim().toDouble()), null)
//            }
//        }
//    }
    // <------------------------------ UI Updates Ends--------------------------------->

}