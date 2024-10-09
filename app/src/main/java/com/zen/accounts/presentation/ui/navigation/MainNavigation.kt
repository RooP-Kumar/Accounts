package com.zen.accounts.presentation.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.gson.Gson
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.ui.screens.common.expense_details_argument
import com.zen.accounts.presentation.ui.screens.common.main_route
import com.zen.accounts.presentation.ui.screens.main.add_expense_item.AddExpenseItem
import com.zen.accounts.presentation.ui.screens.main.addexpense.AddExpense
import com.zen.accounts.presentation.ui.screens.main.expense_detail.ExpenseDetails
import com.zen.accounts.presentation.ui.screens.main.home.Home
import com.zen.accounts.presentation.ui.screens.main.myexpense.MyExpense
import com.zen.accounts.presentation.ui.screens.main.setting.Setting
import com.zen.accounts.presentation.ui.theme.tweenAnimDuration
import com.zen.accounts.presentation.ui.viewmodels.AddExpenseViewModel
import com.zen.accounts.presentation.ui.viewmodels.ExpenseDetailsViewModel
import com.zen.accounts.presentation.ui.viewmodels.HomeViewModel
import com.zen.accounts.presentation.ui.viewmodels.MyExpenseViewModel
import com.zen.accounts.presentation.ui.viewmodels.SettingViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.MainNavigation(appState : AppState, dataStore : UserDataStore) {
    navigation(startDestination = Screen.Home.route, route = main_route) {
        composable(
            route = Screen.AddExpenseScreen.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = tweenAnimDuration)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(tweenAnimDuration)
                )
            },
            popEnterTransition = {
                null
            }
        ) {
            val viewModel : AddExpenseViewModel = hiltViewModel()
            AddExpense(
                appState = appState,
                viewModel = viewModel,
                appState.navController::navigateUp,
                getScreenRouteWithTitle().find { it.route == appState.navController.currentDestination?.route })
        }
        
        composable(
            route = Screen.AddExpenseItemScreen.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = tweenAnimDuration)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(tweenAnimDuration)
                )
            },
            popEnterTransition = {
                null
            }
        ) {
            val viewModel : AddExpenseViewModel = hiltViewModel()
            AddExpenseItem(
                viewModel = viewModel,
                getScreenRouteWithTitle().find { it.route == appState.navController.currentDestination?.route },
                appState.navController::popBackStack
            )
        }
        
        // Home Screen
        composable(
            route = Screen.Home.route
        ) {
            val viewModel : HomeViewModel = hiltViewModel()
            val settingViewModel : SettingViewModel = hiltViewModel()
            Home(appState = appState, viewModel, settingViewModel, dataStore)
        }
        
        composable(
            route = Screen.MyExpenseScreen.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = tweenAnimDuration)
                )
            },
            popEnterTransition = {
                null
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(tweenAnimDuration)
                )
            }
        ) {
            val viewModel : MyExpenseViewModel = hiltViewModel()
            MyExpense(
                drawerState = appState.drawerState,
                viewModel,
                isMonthlyExpense = false,
                navigateUp = appState.navController::navigateUp,
                getScreenRouteWithTitle().find { it.route == appState.navController.currentDestination?.route },
                navigateTo = {
                    appState.navigate(Screen.ExpenseDetailScreen.getRoute(it))
                })
        }
        
        composable(
            route = Screen.ExpenseDetailScreen.route,
            
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = tweenAnimDuration)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(tweenAnimDuration)
                )
            },
            popEnterTransition = {
                null
            },
            arguments = listOf(navArgument(expense_details_argument) { NavType.StringType })
        ) { backStackEntry ->
            val arg = backStackEntry.arguments?.getString(expense_details_argument)
            val expense = if (arg != null) Gson().fromJson(arg, Expense::class.java) else null
            val expenseDetailsViewModel : ExpenseDetailsViewModel = hiltViewModel()
            ExpenseDetails(appState.drawerState,
                expense = expense,
                expenseDetailsViewModel,
                appState.navController::navigateUp,
                getScreenRouteWithTitle().find { it.route == appState.navController.currentDestination?.route })
        }
        
        composable(
            route = Screen.MonthlyExpenseScreen.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = tweenAnimDuration)
                )
            },
            popEnterTransition = {
                null
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(tweenAnimDuration)
                )
            }
        ) {
            val viewModel : MyExpenseViewModel = hiltViewModel()
            MyExpense(
                drawerState = appState.drawerState,
                viewModel,
                isMonthlyExpense = true,
                navigateUp = appState.navController::navigateUp,
                getScreenRouteWithTitle().find { it.route == appState.navController.currentDestination?.route },
                navigateTo = {
                    appState.navigate(Screen.ExpenseDetailScreen.getRoute(it))
                }
            )
        }
        
        // Setting Screen route to navigate
        composable(
            route = Screen.SettingScreen.route,
            enterTransition = {
                slideInHorizontally(tween(tweenAnimDuration)) { it }
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(tweenAnimDuration)
                )
            },
            popEnterTransition = {
                null
            }
        ) {
            val settingViewModel : SettingViewModel = hiltViewModel()
            Setting(
                settingViewModel = settingViewModel,
                dataStore,
                appState.navController::navigateUp,
                getScreenRouteWithTitle().find { it.route == appState.navController.currentDestination?.route }
            )
        }
    }
}
