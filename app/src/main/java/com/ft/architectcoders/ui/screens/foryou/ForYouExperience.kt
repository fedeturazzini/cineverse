package com.ft.architectcoders.ui.screens.foryou

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ft.architectcoders.R
import com.ft.architectcoders.ui.theme.CinemaOrange
import com.ft.architectcoders.ui.theme.DeepSpaceBlue40
import com.ft.architectcoders.ui.theme.GalaxyPurple40
import com.ft.architectcoders.ui.theme.GalaxyPurple80
import com.ft.architectcoders.ui.theme.IndigoDark40
import com.ft.architectcoders.ui.theme.IndigoDark80
import com.ft.architectcoders.ui.theme.NebulaPurple
import com.ft.architectcoders.ui.theme.SpaceNavy
import com.ft.architectcoders.ui.theme.StarBright

enum class ForYouExperience(
    val titleRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
    val gradientColors: List<Color>,
) {
    MARATHON(
        titleRes = R.string.foryou_marathon_title,
        descriptionRes = R.string.foryou_marathon_desc,
        icon = Icons.Filled.LocalFireDepartment,
        gradientColors = listOf(CinemaOrange, DeepSpaceBlue40),
    ),
    MOOD_RADAR(
        titleRes = R.string.foryou_mood_title,
        descriptionRes = R.string.foryou_mood_desc,
        icon = Icons.Filled.EmojiEmotions,
        gradientColors = listOf(GalaxyPurple80, IndigoDark40),
    ),
    MOVIE_DUEL(
        titleRes = R.string.foryou_duel_title,
        descriptionRes = R.string.foryou_duel_desc,
        icon = Icons.Filled.Leaderboard,
        gradientColors = listOf(StarBright, DeepSpaceBlue40),
    ),
    CHALLENGE_RECO(
        titleRes = R.string.foryou_challenge_title,
        descriptionRes = R.string.foryou_challenge_desc,
        icon = Icons.Filled.AutoAwesome,
        gradientColors = listOf(GalaxyPurple40, NebulaPurple),
    ),
    CINEVERSO_60S(
        titleRes = R.string.foryou_cineverso_title,
        descriptionRes = R.string.foryou_cineverso_desc,
        icon = Icons.Filled.Person,
        gradientColors = listOf(IndigoDark80, SpaceNavy),
    ),
    CONVERSATIONAL_SEARCH(
        titleRes = R.string.foryou_search_title,
        descriptionRes = R.string.foryou_search_desc,
        icon = Icons.Filled.Chat,
        gradientColors = listOf(StarBright, GalaxyPurple40),
    ),
}

