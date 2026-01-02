package com.ft.architectcoders

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import com.ft.architectcoders.test.sampleMovies
import com.ft.architectcoders.ui.common.LOADING_TEST_TAG
import com.ft.architectcoders.ui.screens.home.HomeScreen
import com.ft.architectcoders.ui.screens.home.UiState
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenLoadingState_showProgressIndicator() {
        composeTestRule.setContent {
            HomeScreen(
                state = UiState(isLoading = true),
                onMovieClick = {

                },
            )
        }

        composeTestRule.onRoot().printToLog("HomeScreen") // Para ver la estructura de la pantalla

        composeTestRule.onNodeWithTag(LOADING_TEST_TAG)
    }

    @Test
    fun whenErrorState_showError(): Unit = with(composeTestRule) {
        setContent {
            HomeScreen(
                state = UiState(error = "Is error"),
                onMovieClick = {

                },
            )
        }

        onNodeWithText("Is error").assertExists()
    }

    @Test
    fun whenSuccess_showMovies(): Unit = with(composeTestRule) {
        setContent {
            HomeScreen(
                state = UiState(
                    movies = sampleMovies(1,2,3)
                ),
                onMovieClick = {

                },
            )
        }

        onNodeWithText("Movie 3").assertExists()
    }

    @Test
    fun whenMovieClicked_listenerCalled(): Unit = with(composeTestRule) {
        var clickedMovieId = 0
        val movies = sampleMovies(1,2,3)
        setContent {
            HomeScreen(
                state = UiState(
                    movies = movies
                ),
                onMovieClick = {
                    clickedMovieId = it.id
                },
            )
        }

        onNodeWithText("Movie 2").performClick()

        assertEquals(2, clickedMovieId)
    }
}