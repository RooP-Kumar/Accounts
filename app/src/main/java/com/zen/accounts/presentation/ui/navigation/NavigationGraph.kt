package com.zen.accounts.presentation.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zen.accounts.R
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.ui.screens.auth.splash.Splash
import com.zen.accounts.presentation.ui.screens.common.add_expense_item_screen_label
import com.zen.accounts.presentation.ui.screens.common.add_expense_item_screen_route
import com.zen.accounts.presentation.ui.screens.common.add_expense_screen_label
import com.zen.accounts.presentation.ui.screens.common.add_expense_screen_route
import com.zen.accounts.presentation.ui.screens.common.expense_detail_screen_label
import com.zen.accounts.presentation.ui.screens.common.expense_detail_screen_route
import com.zen.accounts.presentation.ui.screens.common.expense_details_argument
import com.zen.accounts.presentation.ui.screens.common.home_screen_label
import com.zen.accounts.presentation.ui.screens.common.home_screen_route
import com.zen.accounts.presentation.ui.screens.common.login_screen_label
import com.zen.accounts.presentation.ui.screens.common.login_screen_route
import com.zen.accounts.presentation.ui.screens.common.monthly_expense_screen_label
import com.zen.accounts.presentation.ui.screens.common.monthly_expense_screen_route
import com.zen.accounts.presentation.ui.screens.common.my_expense_screen_label
import com.zen.accounts.presentation.ui.screens.common.my_expense_screen_route
import com.zen.accounts.presentation.ui.screens.common.register_screen_label
import com.zen.accounts.presentation.ui.screens.common.register_screen_route
import com.zen.accounts.presentation.ui.screens.common.setting_screen_label
import com.zen.accounts.presentation.ui.screens.common.setting_screen_route
import com.zen.accounts.presentation.ui.screens.common.small_logout_button_label
import com.zen.accounts.presentation.ui.screens.common.splash_route
import com.zen.accounts.presentation.ui.screens.common.splash_screen_route
import com.zen.accounts.presentation.ui.screens.common.splash_screen_screen_label

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavigationGraph(
    appState: AppState,
    dataStore : UserDataStore
) {
    NavHost(navController = appState.navController, startDestination = Screen.SplashScreen.route, route = splash_route){
        composable(route = Screen.SplashScreen.route) {
            Splash(appState)
        }

        AuthNavigation(appState)

        MainNavigation(appState = appState, dataStore)

    }
}

sealed class Screen(val route : String, val title: String) {
    data object SplashScreen : Screen(splash_screen_route, splash_screen_screen_label)
    data object LoginScreen : Screen(login_screen_route, login_screen_label)
    data object RegisterScreen : Screen(register_screen_route, register_screen_label)
    data object AddExpenseItemScreen : Screen(add_expense_item_screen_route, add_expense_item_screen_label)
    data object MyExpenseScreen : Screen(my_expense_screen_route, my_expense_screen_label)
    data object AddExpenseScreen : Screen(add_expense_screen_route, add_expense_screen_label)
    data object ExpenseDetailScreen : Screen("$expense_detail_screen_route/{$expense_details_argument}", expense_detail_screen_label) {
        fun getRoute(data: Expense) : String {
            val route = "$expense_detail_screen_route/${data}"
            return route
        }
    }
    data object Home : Screen(home_screen_route, home_screen_label)
    data object SettingScreen : Screen(setting_screen_route, setting_screen_label)
    data object MonthlyExpenseScreen : Screen(monthly_expense_screen_route, monthly_expense_screen_label)

    data object logout : Screen(small_logout_button_label, small_logout_button_label) // only for landscape logout button in drawer layout.
}

fun getScreenRouteWithTitle() : List<Screen> {
    return listOf(
        Screen.LoginScreen,
        Screen.RegisterScreen,
        Screen.AddExpenseItemScreen,
        Screen.MyExpenseScreen,
        Screen.Home,
        Screen.AddExpenseScreen,
        Screen.SettingScreen,
        Screen.ExpenseDetailScreen,
        Screen.MonthlyExpenseScreen
    )
}

// only for landscape mode
fun getScreenRouteWithIcon(isLogin : Boolean) : List<Pair<Screen, Int>> {
    return listOf(
        Pair(
            Screen.Home,
            R.drawable.ic_home
        ),
        Pair(
            Screen.SettingScreen,
            R.drawable.ic_setting
        ),
        if(isLogin) {
            Pair(
                Screen.logout,
                R.drawable.ic_logout
            )
        } else {
            Pair(
                Screen.LoginScreen,
                R.drawable.ic_login
            )
        }
    )
}
