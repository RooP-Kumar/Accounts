package com.zen.accounts.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

// colors
val disabled_color = Color(0xFFD7C1FE)
val enabled_color = Color(0xFF6002EE)
val light_enabled_color = Color(0xFF751AFD)
val primary_color = Color(0xFF7D5FFF)
val secondary_color = Color(0xFFF6F4FD)
val text_color = Color(0xFF545454)
val green_color = Color(0xFF2ED573)
val red_color = Color(0xFFFF4757)

// surface colors
val lightSurface = Color(0xFFF6F6F6)

// Shadow Color
val shadowColor @Composable get() =
    if (isSystemInDarkTheme()) Color.White else Color.Black


// Dark Theme colors ------------------------------------------->
val DarkBackground = Color(0xFF130F40)
val DarkSurface = Color(0xFF151244)
val DarkPrimary = Color(0xFF30336B)
val DarkSecondary = Color(0xFF15174B)


