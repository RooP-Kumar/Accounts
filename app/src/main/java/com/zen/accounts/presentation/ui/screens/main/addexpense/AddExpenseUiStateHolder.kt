package com.zen.accounts.presentation.ui.screens.main.addexpense

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zen.accounts.presentation.ui.screens.common.LoadingState

data class AddExpenseUiStateHolder(
    var title: String = "",
    var expenseItemListAmountTextWidth: Dp = 0.dp,
    var totalExpenseAmount: Double = 0.0,
    var loadingState : LoadingState = LoadingState.IDLE
)
const val AddExpenseUiStateHolderTitle : String = "title"
const val AddExpenseUiStateHolderWidth : String = "expenseItemListAmountTextWidth"
const val AddExpenseUiStateHolderAmount : String = "totalExpenseAmount"
const val AddExpenseUiStateHolderLoadingState : String = "loadingState"
