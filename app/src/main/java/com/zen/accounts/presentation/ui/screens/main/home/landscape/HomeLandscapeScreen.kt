package com.zen.accounts.presentation.ui.screens.main.home.landscape

import android.content.res.Configuration
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zen.accounts.R
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.ui.navigation.Screen
import com.zen.accounts.presentation.ui.screens.common.add_expense_screen_label
import com.zen.accounts.presentation.ui.screens.common.getRupeeString
import com.zen.accounts.presentation.ui.screens.common.home_screen_label
import com.zen.accounts.presentation.ui.screens.common.my_expense_screen_label
import com.zen.accounts.presentation.ui.screens.main.home.HomeUiState
import com.zen.accounts.presentation.ui.theme.AccountsThemes
import com.zen.accounts.presentation.ui.theme.DarkBackground
import com.zen.accounts.presentation.ui.theme.Typography
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.halfGeneralPadding
import com.zen.accounts.presentation.ui.theme.primary_color
import com.zen.accounts.presentation.ui.theme.roundedCornerShape
import com.zen.accounts.presentation.utility.main
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeLandscapeScreen(
    appState: AppState,
    uiState: HomeUiState,
    dataStore: UserDataStore
) {

    uiState.user.value = dataStore.getUser.collectAsState(initial = null).value
    val profilePic = dataStore.getProfilePic.collectAsState(initial = null)
    LaunchedEffect(key1 = profilePic.value) {
        withContext(Dispatchers.IO) {
            if (profilePic.value != null) {
                uiState.profilePic.value =
                    BitmapFactory.decodeByteArray(profilePic.value, 0, profilePic.value!!.size)
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val rowSectionModifierNTH = if (isSystemInDarkTheme())
                                    Modifier
                                        .border(0.5.dp, color = primary_color, shape = roundedCornerShape)
                                else
                                    Modifier
                                        .clip(RoundedCornerShape(generalPadding))
                                        .background(MaterialTheme.colorScheme.background)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {}
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = generalPadding)
            ) {
                IconButton(onClick = {
                    if (appState.drawerState.value != null) {
                        if (appState.drawerState.value!!.isClosed)
                            coroutineScope.launch {
                                appState.drawerState.value!!.open()
                            }
                        else
                            coroutineScope.launch {
                                appState.drawerState.value!!.open()
                            }
                    }
                }) {
                    Icon(
                        painterResource(id = R.drawable.ic_menu),
                        contentDescription = "menu button",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(generalPadding))

                Text(
                    text = home_screen_label,
                    style = Typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .padding(halfGeneralPadding)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(generalPadding),

                ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .then(
                            rowSectionModifierNTH
                        )
                ) {
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
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(
                                horizontal = generalPadding,
                                vertical = generalPadding.times(2)
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween
                    ) {
                        Text(
                            text = "Monthly Expense",
                            style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
                        )

                        Text(
                            text = getRupeeString(uiState.totalAmount.value, showZero = true),
                            style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(generalPadding))

                val btnModifierNTH =
                    if (isSystemInDarkTheme())
                        Modifier
                            .background(DarkBackground)
                            .border(0.5.dp, color = primary_color, shape = roundedCornerShape)
                    else
                        Modifier
                            .background(primary_color)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .then(
                            rowSectionModifierNTH
                        )
                        .padding(generalPadding)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    appState.navigate(Screen.AddExpenseScreen.route)
                                }
                            }
                            .clip(shape = RoundedCornerShape(generalPadding))
                            .then(
                                btnModifierNTH
                            )
                            .padding(
                                horizontal = generalPadding,
                                vertical = generalPadding.times(2)
                            )
                    ) {
                        Text(
                            text = add_expense_screen_label,
                            style = Typography.bodySmall.copy(color = Color.White),
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.height(generalPadding))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                main {
                                    appState.navigate(Screen.MyExpenseScreen.route)
                                }
                            }
                            .clip(shape = RoundedCornerShape(generalPadding))
                            .then(
                                btnModifierNTH
                            )
                            .padding(
                                horizontal = generalPadding,
                                vertical = generalPadding.times(2)
                            )
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
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.TABLET)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.TABLET)
@Composable
private fun PrevHomeLandScape() {
    AccountsThemes {
//        HomeLandscapeScreen(appState = AppState(LocalContext.current), uiState = HomeUiState())
    }
}