package com.zen.accounts.presentation.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.ui.screens.auth.login.Login
import com.zen.accounts.presentation.ui.screens.auth.register.Register
import com.zen.accounts.presentation.ui.screens.common.auth_route
import com.zen.accounts.presentation.ui.theme.tweenAnimDuration
import com.zen.accounts.presentation.ui.viewmodels.LoginScreenViewModel
import com.zen.accounts.presentation.ui.viewmodels.RegisterScreenViewModel

fun NavGraphBuilder.AuthNavigation(
    appState: AppState
) {
    navigation(startDestination = com.zen.accounts.presentation.ui.navigation.Screen.LoginScreen.route, route = auth_route) {
        composable(
            route = com.zen.accounts.presentation.ui.navigation.Screen.LoginScreen.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = tweenAnimDuration))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(tweenAnimDuration))
            },
            popEnterTransition = {
                null
            }
        ) {
            val viewmodel = hiltViewModel<LoginScreenViewModel>()
            Login(appState, viewmodel, currentScreen = com.zen.accounts.presentation.ui.navigation.getScreenRouteWithTitle()
                .find { it.route == appState.navController.currentDestination?.route }, appState.navController::navigateUp)
        }

        composable(
            route = com.zen.accounts.presentation.ui.navigation.Screen.RegisterScreen.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = tweenAnimDuration))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(tweenAnimDuration))
            },
            popEnterTransition = {
                null
            }) {
            val viewmodel : RegisterScreenViewModel = hiltViewModel()
            Register(appState, viewmodel, currentScreen = com.zen.accounts.presentation.ui.navigation.getScreenRouteWithTitle()
                .find { it.route == appState.navController.currentDestination?.route }, appState.navController::navigateUp)
        }
    }
}