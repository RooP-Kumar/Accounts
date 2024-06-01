package com.zen.accounts.ui.screens.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.zen.accounts.R
import com.zen.accounts.states.AppState
import com.zen.accounts.ui.navigation.Screen
import com.zen.accounts.ui.screens.common.add_expense_screen_label
import com.zen.accounts.ui.screens.common.getRupeeString
import com.zen.accounts.ui.screens.common.home_screen_label
import com.zen.accounts.ui.screens.common.my_expense_screen_label
import com.zen.accounts.ui.theme.Typography
import com.zen.accounts.ui.theme.background
import com.zen.accounts.ui.theme.disabled_color
import com.zen.accounts.ui.theme.enabled_color
import com.zen.accounts.ui.theme.generalPadding
import com.zen.accounts.ui.theme.halfGeneralPadding
import com.zen.accounts.ui.theme.onBackground
import com.zen.accounts.ui.viewmodels.HomeViewModel
import com.zen.accounts.utility.io
import com.zen.accounts.utility.main
import kotlinx.coroutines.launch

data class HomeUiState(
    val totalAmount : MutableState<Double> = mutableDoubleStateOf(0.0)
)

@Composable
fun Home(
    appState: AppState,
    viewModel: HomeViewModel
) {

    val coroutineScope = rememberCoroutineScope()
    val uiState = viewModel.homeUiState
    val monthlyExpense = viewModel.monthlyExpense.collectAsState(initial = listOf())

    DisposableEffect(key1 = monthlyExpense.value.size) {
        if(monthlyExpense.value.isNotEmpty())
            monthlyExpense.value.forEach {
                uiState.totalAmount.value += it.totalAmount
            }
        onDispose { uiState.totalAmount.value = 0.0 }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(generalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = home_screen_label,
                style = Typography.headlineSmall.copy(color = onBackground),
                modifier = Modifier
                    .padding(halfGeneralPadding)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = "setting icon",
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(generalPadding))
                    .clickable {
                        coroutineScope.launch {
                            appState.navController.navigate(Screen.SettingScreen.route)
                        }
                    }
                    .padding(halfGeneralPadding),
                tint = onBackground
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(generalPadding)
                .clickable {
                    main {
                        appState.navigate(Screen.MonthlyExpenseScreen.route)
                    }
                }
                .clip(RoundedCornerShape(generalPadding))
                .background(disabled_color)
                .padding(horizontal = generalPadding, vertical = generalPadding.times(2)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Text(
                text = "Monthly Expense",
                style = Typography.bodyLarge.copy(color = onBackground)
            )

            Text(
                text = getRupeeString(uiState.totalAmount.value, showZero = true),
                style = Typography.bodyLarge.copy(color = onBackground)
            )
        }

        Row(
            modifier = Modifier
                .padding(start = generalPadding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = generalPadding, end = generalPadding)
                    .clickable {
                        coroutineScope.launch {
                            appState.navigate(Screen.AddExpenseScreen.route)
                        }
                    }
                    .clip(shape = RoundedCornerShape(generalPadding))
                    .background(enabled_color)
                    .padding(horizontal = generalPadding, vertical = generalPadding.times(2))
            ) {
                Text(
                    text = add_expense_screen_label,
                    style = Typography.bodySmall.copy(color = Color.White),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = generalPadding, end = generalPadding)
                    .clickable {
                        main {
                            appState.navigate(Screen.MyExpenseScreen.route)
                        }
                    }
                    .clip(shape = RoundedCornerShape(generalPadding))
                    .background(enabled_color)
                    .padding(horizontal = generalPadding, vertical = generalPadding.times(2))
            ) {
                Text(
                    text = my_expense_screen_label,
                    style = Typography.bodySmall.copy(color = Color.White),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

        }
    }
}
