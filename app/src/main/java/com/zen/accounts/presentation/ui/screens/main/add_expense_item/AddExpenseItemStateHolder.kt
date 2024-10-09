package com.zen.accounts.presentation.ui.screens.main.add_expense_item

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zen.accounts.R
import com.zen.accounts.presentation.ui.screens.common.LoadingState

data class AddExpenseItemStateHolder(
    var title: String = "",
    var amount: String = "",
    var showAmountDropDown: Boolean = false,
    var dropDownRowWidth: Dp = 0.dp,
    var dropDownIcon: Int = R.drawable.ic_drop_down,
    var loadingState : LoadingState = LoadingState.IDLE,
    var confirmationDialogState : Boolean = false
)

const val addExpenseItemStateHolder_title = "title"
const val addExpenseItemStateHolder_amount = "amount"
const val addExpenseItemStateHolder_show_amount_drop_down = "showAmountDropDown"
const val addExpenseItemStateHolder_drop_down_row_width = "dropDownRowWidth"
const val addExpenseItemStateHolder_drop_down_icon = "dropDownIcon"
const val addExpenseItemStateHolder_confirmation_state = "confirmationDialogState"
const val addExpenseItemStateHolder_loading_state = "loadingState"
