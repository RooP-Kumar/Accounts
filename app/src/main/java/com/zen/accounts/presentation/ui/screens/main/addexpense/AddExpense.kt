package com.zen.accounts.presentation.ui.screens.main.addexpense

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zen.accounts.presentation.CommonUIStateHolder
import com.zen.accounts.R
import com.zen.accounts.data.db.model.ExpenseItem
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.ui.navigation.Screen
import com.zen.accounts.presentation.ui.screens.common.GeneralButton
import com.zen.accounts.presentation.ui.screens.common.GeneralSnackBar
import com.zen.accounts.presentation.ui.screens.common.LoadingDialog
import com.zen.accounts.presentation.ui.screens.common.LoadingState
import com.zen.accounts.presentation.ui.screens.common.TopAppBar
import com.zen.accounts.presentation.ui.screens.common.getRupeeString
import com.zen.accounts.presentation.ui.theme.AccountsThemes
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.halfGeneralPadding
import com.zen.accounts.presentation.ui.theme.primary_color
import com.zen.accounts.presentation.ui.theme.red_color
import com.zen.accounts.presentation.ui.theme.topBarHeight
import com.zen.accounts.presentation.ui.viewmodels.AddExpenseViewModel
import com.zen.accounts.presentation.utility.generalBorder
import java.util.Date

@Composable
fun AddExpense(
    appState: AppState,
    viewModel: AddExpenseViewModel,
    navigateUp : () -> Boolean,
    currentScreen: Screen?
) {
    val addExpenseUiStateHolder = viewModel.addExpenseUiStateHolder.collectAsState()
    val commonUiStateHolder = viewModel.commonUIStateHolder.collectAsState()
    val allExpenseItems = viewModel.allExpenseItem.collectAsState(initial = arrayListOf())

    LaunchedEffect(key1 = allExpenseItems.value.size) {
        if (allExpenseItems.value.isNotEmpty()) {
            var totalAmount = 0.0
            allExpenseItems.value.forEach { item ->
                totalAmount += (item.itemAmount ?: 0.0)
            }
            viewModel.updateAddExpenseStateValue(
                totalAmount,
                AddExpenseUiStateHolderAmount
            )
        } else {
            viewModel.updateAddExpenseStateValue(0.0, AddExpenseUiStateHolderAmount)
        }
    }

    MainUi(
        appState = appState,
        addExpenseUiStateHolder = addExpenseUiStateHolder.value,
        commonUiStateHolder = commonUiStateHolder.value,
        allExpenseItems = allExpenseItems.value,
        viewModel::updateAddExpenseStateValue,
        viewModel::deleteExpenseItemsFromLocalDatabase,
        viewModel::onAddExpenseClick,
        navigateUp,
        currentScreen
    )
}

@Composable
private fun MainUi(
    appState: AppState?,
    addExpenseUiStateHolder: AddExpenseUiStateHolder,
    commonUiStateHolder: CommonUIStateHolder,
    allExpenseItems: List<ExpenseItem>?,
    updateAddExpenseStateValue: (Any, String) -> Unit,
    deleteExpenseItemsFromLocalDatabase: () -> Unit,
    onAddExpenseClick: (List<ExpenseItem>) -> Unit,
    navigateUp : () -> Boolean,
    currentScreen: Screen?
) {
    val localDensity = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp

    LoadingDialog(
        loadingState = addExpenseUiStateHolder.loadingState,
        onSuccess = {
            deleteExpenseItemsFromLocalDatabase()
            updateAddExpenseStateValue("", AddExpenseUiStateHolderTitle)
            updateAddExpenseStateValue(LoadingState.IDLE, AddExpenseUiStateHolderLoadingState)
        }
    )

    // This for loop is just fro to calculate the maximum width of the text element.
    // Which I can use in the LazyColumn item to set the Rupee Text width.
    allExpenseItems?.forEach {
        Text(
            text = getRupeeString(it.itemAmount ?: 0.0),
            modifier = Modifier
                .onGloballyPositioned {
                    val tempDp = with(localDensity) {
                        it.size.width.toDp()
                    }
                    if (tempDp > addExpenseUiStateHolder.expenseItemListAmountTextWidth) {
                        updateAddExpenseStateValue(tempDp, AddExpenseUiStateHolderWidth)
                    }
                }
                .zIndex(-100f) // zIndex is -1 so that it does not show on the screen.
                .alpha(0f)
        )
    }
    // End

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {}
    ) {
        if (screenWidth <= 500) {
            TopAppBar(
                appState?.drawerState ?: AppState(LocalContext.current).drawerState,
                btnText = "ADD",
                buttonEnableCondition = true,
                onClick = {
                    onAddExpenseClick(allExpenseItems?: listOf())
                },
                navigateUp = navigateUp,
                currentScreen = currentScreen
            )
        } else {
            TopAppBar(appState?.drawerState ?: AppState(LocalContext.current).drawerState, navigateUp = navigateUp, currentScreen = currentScreen)
        }

        GeneralSnackBar(
            visible = commonUiStateHolder.showSnackBar,
            text = commonUiStateHolder.snackBarText,
            containerColor = red_color
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topBarHeight)
                .pointerInput(Unit) {}
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(Unit) {}
                    .background(MaterialTheme.colorScheme.background)
            ) {

                if (screenWidth <= 500) {
                    UpperTitleSection(
                        title = addExpenseUiStateHolder.title
                    ) {
                        updateAddExpenseStateValue(it, AddExpenseUiStateHolderTitle)
                    }
                } else {
                    AddExpenseItemTitleSection()
                }

                ItemListLayout(
                    appState ?: AppState(LocalContext.current),
                    addExpenseUiStateHolder.expenseItemListAmountTextWidth,
                    allExpenseItems ?: listOf()
                )
            }

            if (screenWidth > 500) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {

                    UpperTitleSection(
                        title = addExpenseUiStateHolder.title
                    ) {
                        updateAddExpenseStateValue(it, AddExpenseUiStateHolderTitle)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(generalPadding)
                                .padding(horizontal = halfGeneralPadding),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Amount : ",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                            )

                            Text(
                                text = getRupeeString(addExpenseUiStateHolder.totalExpenseAmount),
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
                            )
                        }
                        GeneralButton(
                            text = "ADD EXPENSE",
                            modifier = Modifier
                                .padding(bottom = halfGeneralPadding, end = generalPadding)
                                .align(Alignment.BottomEnd)
                        ) {
                            onAddExpenseClick(allExpenseItems ?: listOf())
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ItemListLayout(
    appState: AppState,
    rupeeTextWidth: Dp,
    allExpenseItems: List<ExpenseItem>
) {

    Box {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .clip(RoundedCornerShape(generalPadding))
                .padding(
                    start = generalPadding,
                    end = generalPadding,
                    bottom = generalPadding,
                    top = halfGeneralPadding
                )
                .generalBorder(width = 0.5.dp)
        ) {
            if (allExpenseItems.isEmpty()) {
                val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_list))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(
                        composition = lottieComposition,
                        modifier = Modifier
                            .size(350.dp, 350.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "No items added!",
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                }

            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = halfGeneralPadding)
                ) {
                    itemsIndexed(allExpenseItems) { _, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = generalPadding,
                                    end = generalPadding,
                                    top = halfGeneralPadding,
                                    bottom = halfGeneralPadding
                                )
                                .generalBorder()
                                .background(MaterialTheme.colorScheme.secondary )
                                .padding(generalPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.itemTitle,
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = generalPadding)
                            )

                            Text(
                                text = getRupeeString(item.itemAmount!!),
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                                modifier = Modifier
                                    .width(rupeeTextWidth)
                            )
                        }

                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { appState.navController.navigate(Screen.AddExpenseItemScreen.route) },
            shape = RoundedCornerShape(50),
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = halfGeneralPadding, end = halfGeneralPadding)
                .border(0.5.dp, color = primary_color, shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add expense item",
                tint = Color.White
            )
        }
    }

}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun PreviewMainUI() {
    AccountsThemes {
        MainUi(
            appState = AppState(LocalContext.current),
            addExpenseUiStateHolder = AddExpenseUiStateHolder(),
            commonUiStateHolder = CommonUIStateHolder(),
            allExpenseItems = listOf(ExpenseItem(0L, "", 0.0, Date())),
            updateAddExpenseStateValue = {_, _ ->},
            deleteExpenseItemsFromLocalDatabase = {},
            onAddExpenseClick = {},
            navigateUp = { true },
            currentScreen = Screen.AddExpenseScreen
        )
    }
}


