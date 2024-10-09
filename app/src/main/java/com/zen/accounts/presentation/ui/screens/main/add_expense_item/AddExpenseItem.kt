package com.zen.accounts.presentation.ui.screens.main.add_expense_item

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zen.accounts.presentation.CommonUIStateHolder
import com.zen.accounts.presentation.ui.navigation.Screen
import com.zen.accounts.presentation.ui.screens.common.GeneralButton
import com.zen.accounts.presentation.ui.screens.common.GeneralDialog
import com.zen.accounts.presentation.ui.screens.common.GeneralEditText
import com.zen.accounts.presentation.ui.screens.common.GeneralSnackBar
import com.zen.accounts.presentation.ui.screens.common.LoadingDialog
import com.zen.accounts.presentation.ui.screens.common.TopAppBar
import com.zen.accounts.presentation.ui.screens.common.add_item_button_label
import com.zen.accounts.presentation.ui.theme.AccountsThemes
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.halfGeneralPadding
import com.zen.accounts.presentation.ui.theme.red_color
import com.zen.accounts.presentation.ui.theme.topBarHeight
import com.zen.accounts.presentation.ui.viewmodels.AddExpenseViewModel

@Composable
fun AddExpenseItem(
    viewModel: AddExpenseViewModel,
    currentScreen: Screen? = null,
    navigateUp : () -> Boolean
) {
    val addExpenseItemUiStateHolder = viewModel.addExpenseItemUiStateHolder.collectAsState()
    val commonUIStateHolder = viewModel.commonUIStateHolder.collectAsState()

    BackHandler(
        enabled = addExpenseItemUiStateHolder.value.title.isNotEmpty() ||
                addExpenseItemUiStateHolder.value.amount.isNotEmpty())
    {
        viewModel.updateStateValue(true, addExpenseItemStateHolder_confirmation_state)
    }

    LoadingDialog(
        loadingState = addExpenseItemUiStateHolder.value.loadingState,
        onSuccess = { navigateUp() }
    )

    MainUI(
        viewModel.appState.drawerState,
        addExpenseItemStateHolder = addExpenseItemUiStateHolder.value,
        commonUIStateHolder = commonUIStateHolder.value,
        currentScreen,
        navigateUp,
        viewModel::onAddExpenseItemClick,
        updateStateValues = viewModel::updateStateValue
    )
}

@Composable
private fun MainUI(
    drawerState: MutableState<DrawerState?>? = null,
    addExpenseItemStateHolder: AddExpenseItemStateHolder? = null,
    commonUIStateHolder: CommonUIStateHolder? = null,
    currentScreen: Screen? = null,
    navigateUp: () -> Boolean,
    onAddExpenseItemClick : () -> Unit,
    updateStateValues: (Any, String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {}
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(drawerState = drawerState, currentScreen = currentScreen, navigateUp = navigateUp)
        
        GeneralSnackBar(
            visible = commonUIStateHolder?.showSnackBar ?: false,
            text = commonUIStateHolder?.snackBarText ?: "",
            containerColor = red_color
        )

        GeneralDialog(
            showDialog = addExpenseItemStateHolder?.confirmationDialogState ?: false,
            onDismissRequest = {
                updateStateValues(false, addExpenseItemStateHolder_confirmation_state)
            }
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(generalPadding)
            ) {
                Text(
                    text = "Are you sure?\nYou want to go back, Your data will be lost.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(generalPadding))
                Row {
                    GeneralButton(
                        text = "NO",
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        updateStateValues(false, addExpenseItemStateHolder_confirmation_state)
                    }
                    
                    Spacer(modifier = Modifier.width(generalPadding))

                    GeneralButton(
                        text = "YES",
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        navigateUp()
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topBarHeight)
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = generalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            GeneralEditText(
                text = addExpenseItemStateHolder?.title ?: "",
                onValueChange = {
                    updateStateValues(it, addExpenseItemStateHolder_title)
                },
                modifier = Modifier.width(400.dp),
                placeholderText = "Title",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
            GeneralEditText(
                text = addExpenseItemStateHolder?.amount ?: "",
                onValueChange = {
                    updateStateValues(it, addExpenseItemStateHolder_amount)
                },
                modifier = Modifier.width(400.dp),
                placeholderText = "Amount",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            GeneralButton(
                text = add_item_button_label,
                modifier = Modifier
                    .width(400.dp)
                    .padding(horizontal = generalPadding)
                    .padding(top = halfGeneralPadding),
                onClick = onAddExpenseItemClick
            )

        }
    }

}

@Preview(name = "dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "light mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun ShowPrev() {
    AccountsThemes {
        MainUI(
            navigateUp = { false },
            onAddExpenseItemClick = {  },
            updateStateValues = {_,_ ->},
            currentScreen = Screen.AddExpenseItemScreen
        )
    }
}