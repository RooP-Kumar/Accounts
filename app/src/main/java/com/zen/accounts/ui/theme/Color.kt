package com.zen.accounts.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

// colors
val disabled_color = Color(0xFFD7C1FE)
val enabled_color = Color(0xFF6002EE)
val light_enabled_color = Color(0xFF751AFD)
//val primary_color = Color(0xFFA55EEA)
val primary_color = Color(0xFF7D5FFF)
val secondary_color = Color(0xFFF6F4FD)
val very_light_primary_color = Color(0xFFF8F5FF)
val text_color = Color(0xFF545454)
val border_color = Color(0xFFD1D1D1)
val green_color = Color(0xFF2ED573)
val red_color = Color(0xFFFF4757)

// surface colors
val lightSurface = Color(0xFFF6F6F6)
val darkSurface = Color(0xFF0F0F0F)

// Shadow Color
val shadowColor @Composable get() =
    if (isSystemInDarkTheme()) Color.White else Color.Black


