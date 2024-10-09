package com.zen.accounts.presentation.ui.screens.main.home

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.data.db.model.User
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.ui.screens.main.home.landscape.HomeLandscapeScreen
import com.zen.accounts.presentation.ui.screens.main.home.portrait.HomePortraitScreen
import com.zen.accounts.presentation.ui.viewmodels.HomeViewModel
import com.zen.accounts.presentation.ui.viewmodels.SettingViewModel

data class HomeUiState(
    val totalAmount : MutableState<Double> = mutableDoubleStateOf(0.0),
    val user : MutableState<User?> = mutableStateOf(null),
    val showImagePickerOption: MutableState<Boolean> = mutableStateOf(false),
    val profilePic: MutableState<Bitmap?> = mutableStateOf(null),
)

@Composable
fun Home(
    appState: AppState,
    viewModel: HomeViewModel,
    settingViewModel: SettingViewModel,
    dataStore: UserDataStore
) {
    val uiState = viewModel.homeUiState
    val monthlyExpense = viewModel.monthlyExpense.collectAsState(initial = listOf())
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    DisposableEffect(key1 = monthlyExpense.value.size) {
        if(monthlyExpense.value.isNotEmpty())
            monthlyExpense.value.forEach {
                uiState.totalAmount.value += it.totalAmount
            }
        onDispose { uiState.totalAmount.value = 0.0 }
    }

    if(screenWidth <= 500.dp) {
        HomePortraitScreen(uiState = uiState, navigateTo = appState::navigate)
    } else {
        LaunchedEffect(key1 = Unit) {
            settingViewModel.getBackupPlan()
        }
        HomeLandscapeScreen(appState = appState, uiState, dataStore)
    }
}
