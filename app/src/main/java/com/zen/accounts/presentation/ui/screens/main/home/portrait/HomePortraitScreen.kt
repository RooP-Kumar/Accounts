package com.zen.accounts.presentation.ui.screens.main.home.portrait

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zen.accounts.R
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
import com.zen.accounts.presentation.ui.theme.secondary_color
import com.zen.accounts.presentation.utility.main
import kotlinx.coroutines.launch

@Composable
fun HomePortraitScreen(
    navigateTo: (String) -> Unit,
    uiState: HomeUiState
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(generalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = R.drawable.ic_logo,
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.width(halfGeneralPadding))

            Text(
                text = home_screen_label,
                style = Typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(vertical = halfGeneralPadding)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = "setting icon",
                modifier = Modifier
                    .then(
                        if (isSystemInDarkTheme()) {
                            Modifier.border(0.5.dp, color = primary_color, shape = CircleShape).background(DarkBackground)
                        } else {
                            Modifier.clip(shape = CircleShape).background(secondary_color)
                        }
                    )
                    .clickable {
                        coroutineScope.launch {
                            navigateTo(Screen.SettingScreen.route)
                        }
                    }
                    .padding(halfGeneralPadding),
                tint = primary_color
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(generalPadding)
                .clickable {
                    main {
                        navigateTo(Screen.MonthlyExpenseScreen.route)
                    }
                }
                .clip(RoundedCornerShape(generalPadding))
                .background(MaterialTheme.colorScheme.secondary)
                .padding(horizontal = generalPadding, vertical = generalPadding.times(2)),
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

        val btnModifierATTH = if (isSystemInDarkTheme()) {
            Modifier
                .border(0.5.dp, color = primary_color, shape = roundedCornerShape)
                .drawBehind {
                    drawRoundRect(
                        color = DarkBackground
                    )
                }
        } else {
            Modifier
                .clip(shape = RoundedCornerShape(generalPadding))
                .drawBehind {
                    drawRoundRect(
                        color = primary_color
                    )
                }
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
                            navigateTo(Screen.AddExpenseScreen.route)
                        }
                    }
                    .then(btnModifierATTH)
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

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = generalPadding, end = generalPadding)
                    .clickable {
                        main {
                            navigateTo(Screen.MyExpenseScreen.route)
                        }
                    }
                    .then(btnModifierATTH)
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

