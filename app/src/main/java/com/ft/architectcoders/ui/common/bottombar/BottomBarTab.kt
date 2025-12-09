package com.ft.architectcoders.ui.common.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.LocalMovies
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarTab(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val route: String,
) {
    data object Profile : BottomBarTab(
        title = "Perfil",
        icon = Icons.Rounded.Person,
        color = Color(0xFF667EEA),
        route = "profile",
    )

    data object Home : BottomBarTab(
        title = "Cartelera",
        icon = Icons.Rounded.LocalMovies,
        color = Color(0xFFFF6B6B),
        route = "home",
    )

    data object ForYou : BottomBarTab(
        title = "Para Ti",
        icon = Icons.Rounded.AutoAwesome,
        color = Color(0xFFF093FB),
        route = "for_you",
    )
}

val bottomBarTabs =
    listOf(
        BottomBarTab.Profile,
        BottomBarTab.Home,
        BottomBarTab.ForYou,
    )
