package com.zen.accounts.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zen.accounts.R
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.data.db.model.User
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.ui.navigation.NavigationGraph
import com.zen.accounts.presentation.ui.navigation.getScreenRouteWithIcon
import com.zen.accounts.presentation.ui.screens.common.auth_route
import com.zen.accounts.presentation.ui.screens.common.small_logout_button_label
import com.zen.accounts.presentation.ui.screens.main.addexpense.HorizontalLineOnBackground
import com.zen.accounts.presentation.ui.screens.main.setting.ProfileSection
import com.zen.accounts.presentation.ui.screens.main.setting.ScreenDialogs
import com.zen.accounts.presentation.ui.theme.AccountsThemes
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.viewmodels.SettingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CommonUIStateHolder(
    var showSnackBar : Boolean = false,
    var snackBarText : String = ""
)

@Composable
fun MainApp(
    settingViewModel: SettingViewModel,
    dataStore: UserDataStore
) {
    settingViewModel.appState.darkMode.value = isSystemInDarkTheme()
    val navController = rememberNavController()
    settingViewModel.appState.navController = navController

    MainUI(
        settingViewModel,
        dataStore
    )
}

@Composable
private fun MainUI(
    settingViewModel: SettingViewModel,
    dataStore: UserDataStore
) {
    AccountsThemes {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val appState = settingViewModel.appState

        if (screenWidth <= 500.dp) {
            NavigationGraph(appState, dataStore)
        } else {
            DrawerContent(appState, settingViewModel, dataStore)
        }
    }
}

@Composable
fun DrawerContent(
    appState: AppState,
    settingViewModel: SettingViewModel,
    dataStore: UserDataStore
) {
    val coroutineScope = rememberCoroutineScope()
    val user = dataStore.getUser.collectAsState(initial = null)
    val showImagePickerOption = remember { mutableStateOf(false) }
    val profilePicBitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    settingViewModel.appState.drawerState.value = drawerState

    val profilePic = dataStore.getProfilePic.collectAsState(initial = null)
    LaunchedEffect(key1 = profilePic.value) {
        withContext(Dispatchers.IO) {
            if (profilePic.value != null) {
                profilePicBitmap.value = BitmapFactory.decodeByteArray(
                    profilePic.value,
                    0,
                    profilePic.value!!.size
                )
            }
        }
    }

    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = false,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                    drawerContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Profile",
                            style = MaterialTheme.typography.bodyMedium.copy(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier.padding(generalPadding)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = {
                            coroutineScope.launch {
                                appState.drawerState.value?.close()
                            }
                        }) {
                            Icon(
                                painterResource(id = R.drawable.ic_menu_opened),
                                contentDescription = "menu close button",
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    HorizontalLineOnBackground()

                    Column(
                        modifier = Modifier
                            .verticalScroll(
                                state = scrollState
                            )
                    ) {
                        ProfileSection(user = user.value, showImagePickerOption = showImagePickerOption, profilePicBitmap = profilePicBitmap)

                        DrawerBody(appState = appState, user = user, settingViewModel = settingViewModel, dataStore = dataStore)

                        Spacer(modifier = Modifier.height(generalPadding))
                    }

                }
            }
        ) {
            NavigationGraph(appState, dataStore)
        }

        ScreenDialogs(
            uiState = settingViewModel.settingUIState,
            settingViewModel::logout,
            settingViewModel::logoutConfirmation
        )


    }
}

@Composable
fun DrawerBody(
    appState: AppState,
    dataStore: UserDataStore,
    user : State<User?>,
    settingViewModel: SettingViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState = settingViewModel.settingUIState
    val screens = getScreenRouteWithIcon(user.value?.isAuthenticated ?: false)
    val backStackEntry = appState.navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry.value?.destination?.route
    screens.forEach {
        NavigationDrawerItem(
            modifier = Modifier
                .padding(horizontal = generalPadding)
                .padding(top = generalPadding),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                unselectedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            ),
            icon = {
                Icon(
                    painterResource(id = it.second),
                    contentDescription = "setting button",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            },
            label = {
                Text(
                    text = it.first.title,
                    style = MaterialTheme.typography.bodyMedium.copy(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
                )
            },
            selected = currentDestination == it.first.route,
            onClick = {
                coroutineScope.launch {
                    if (it.first.route != small_logout_button_label) appState.drawerNavigate(
                        it.first.route
                    )
                    else {
                        coroutineScope.launch {
                            val currentUser =
                                dataStore.getUser()
                            if (currentUser != null && currentUser.isAuthenticated)
                                uiState.showLogoutPopUp.value = true
                            else
                                appState.navController.navigate(
                                    auth_route
                                )
                        }
                    }
                }
            }
        )
    }
}
