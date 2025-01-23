package com.zen.accounts.presentation.ui.screens.main.home

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.data.db.model.User
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.ui.screens.main.home.landscape.HomeLandscapeScreen
import com.zen.accounts.presentation.ui.screens.main.home.portrait.HomePortraitScreen
import com.zen.accounts.presentation.ui.viewmodels.HomeViewModel
import com.zen.accounts.presentation.ui.viewmodels.SettingViewModel
import kotlinx.coroutines.launch

data class HomeUiState(
    val totalAmount : MutableState<Double> = mutableDoubleStateOf(0.0),
    val user : MutableState<User?> = mutableStateOf(null),
    val showImagePickerOption: MutableState<Boolean> = mutableStateOf(false),
    val profilePic: MutableState<Bitmap?> = mutableStateOf(null),
    val graphData : MutableState<Pair<List<Double>?, List<String>?>?> = mutableStateOf(null),
    val showMonthlyProgressBar : MutableState<Boolean> = mutableStateOf(false)
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
    
    LaunchedEffect(key1 = Unit) {
        viewModel.getFilteredGraphData(null)
    }

    DisposableEffect(key1 = monthlyExpense.value.size) {
        if(monthlyExpense.value.isNotEmpty())
            monthlyExpense.value.forEach {
                uiState.totalAmount.value += it.totalAmount
            }
        onDispose { uiState.totalAmount.value = 0.0 }
    }

    if(screenWidth <= 500.dp) {
        val coroutineScope = rememberCoroutineScope()
        HomePortraitScreen(uiState = uiState, navigateTo = appState::navigate, filterType = viewModel.storedFilterType) { filterType -> 
            coroutineScope.launch { 
                viewModel.getFilteredGraphData(filterType)
            }
        }
    } else {
        LaunchedEffect(key1 = Unit) {
            settingViewModel.getBackupPlan()
        }
        HomeLandscapeScreen(appState = appState, uiState, dataStore)
    }
}
