package com.ft.architectcoders

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ft.architectcoders.test.sampleAiReview
import com.ft.architectcoders.test.sampleCast
import com.ft.architectcoders.test.sampleMovie
import com.ft.architectcoders.test.sampleMovieVideo
import com.ft.architectcoders.ui.common.LOADING_TEST_TAG
import com.ft.architectcoders.ui.screens.detail.AiReviewUiState
import com.ft.architectcoders.ui.screens.detail.MOVIE_DETAIL_AI_REVIEW_CARD_TAG
import com.ft.architectcoders.ui.screens.detail.MOVIE_DETAIL_AI_REVIEW_ERROR_CARD_TAG
import com.ft.architectcoders.ui.screens.detail.MOVIE_DETAIL_AI_REVIEW_LOADING_CARD_TAG
import com.ft.architectcoders.ui.screens.detail.MOVIE_DETAIL_BACK_BUTTON_TAG
import com.ft.architectcoders.ui.screens.detail.MOVIE_DETAIL_BACKDROP_IMAGE_TAG
import com.ft.architectcoders.ui.screens.detail.MOVIE_DETAIL_CAST_CAROUSEL_TAG
import com.ft.architectcoders.ui.screens.detail.MOVIE_DETAIL_FAVORITE_FAB_TAG
import com.ft.architectcoders.ui.screens.detail.MOVIE_DETAIL_TRAILERS_SECTION_TAG
import com.ft.architectcoders.ui.screens.detail.MovieDetailScreen
import com.ft.architectcoders.ui.screens.detail.MovieDetailUiState
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class MovieDetailScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenLoadingState_showProgressIndicator() {
        composeTestRule.setContent {
            MovieDetailScreen(
                state = MovieDetailUiState(isLoadingMovie = true),
                onBack = { },
                onFavoriteClick = {},
            )
        }

        composeTestRule.onNodeWithTag(LOADING_TEST_TAG).assertExists()
    }

    @Test
    fun whenMovieLoaded_showMovieDetails(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1)
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithText(movie.title).assertExists()
            onNodeWithText(movie.originalTitle).assertExists()
            onNodeWithText(movie.overview).assertExists()
            onNodeWithText("Release date: ${movie.releaseDate}").assertExists()
            onNodeWithText("Sinopsis").assertExists()
            onNodeWithTag(MOVIE_DETAIL_BACKDROP_IMAGE_TAG).assertExists()
        }

    @Test
    fun whenMovieIsNull_showEmptyState(): Unit =
        with(composeTestRule) {
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = null,
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_BACKDROP_IMAGE_TAG).assertDoesNotExist()
        }

    @Test
    fun whenCastNotEmpty_showCastCarousel(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1)
            val cast = listOf(sampleCast(1), sampleCast(2))
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            cast = cast,
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_CAST_CAROUSEL_TAG).assertExists()
            onNodeWithText("Reparto Principal").assertExists()
            onNodeWithText("Actor 1").assertExists()
            onNodeWithText("Actor 2").assertExists()
        }

    @Test
    fun whenCastEmpty_hideCastCarousel(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1)
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            cast = emptyList(),
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_CAST_CAROUSEL_TAG).assertDoesNotExist()
        }

    @Test
    fun whenVideosNotEmpty_showTrailersSection(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1)
            val videos =
                listOf(
                    sampleMovieVideo("1"),
                    sampleMovieVideo("2"),
                )
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            videos = videos,
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_TRAILERS_SECTION_TAG).assertExists()
            onNodeWithText("Tráilers y Videos").assertExists()
        }

    @Test
    fun whenVideosEmpty_hideTrailersSection(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1)
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            videos = emptyList(),
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_TRAILERS_SECTION_TAG).assertDoesNotExist()
        }

    @Test
    fun whenAiReviewSuccess_showAiReviewCard(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1)
            val aiReview = sampleAiReview(rating = 4.5f, quote = "Una película excelente")
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            aiReviewState = AiReviewUiState.Success(aiReview),
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_AI_REVIEW_CARD_TAG).assertExists()
            onNodeWithText("Reseña AI").assertExists()
            onNodeWithText("\"${aiReview.quote}\"").assertExists()
        }

    @Test
    fun whenAiReviewLoading_showLoadingCard(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1)
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            aiReviewState = AiReviewUiState.Loading,
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_AI_REVIEW_LOADING_CARD_TAG).assertExists()
            onNodeWithText("Generando reseña con IA…").assertExists()
        }

    @Test
    fun whenAiReviewError_showErrorCard(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1)
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            aiReviewState = AiReviewUiState.Error("Error message"),
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_AI_REVIEW_ERROR_CARD_TAG).assertExists()
            onNodeWithText("Reseña AI Premium").assertExists()
        }

    @Test
    fun whenAiReviewNotRequested_hideAiReview(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1)
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            aiReviewState = AiReviewUiState.NotRequested,
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_AI_REVIEW_CARD_TAG).assertDoesNotExist()
            onNodeWithTag(MOVIE_DETAIL_AI_REVIEW_LOADING_CARD_TAG).assertDoesNotExist()
            onNodeWithTag(MOVIE_DETAIL_AI_REVIEW_ERROR_CARD_TAG).assertDoesNotExist()
        }

    @Test
    fun whenBackButtonClicked_callsOnBack(): Unit =
        with(composeTestRule) {
            var backClicked = false
            val movie = sampleMovie(1)
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            isLoadingMovie = false,
                        ),
                    onBack = {
                        backClicked = true
                    },
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_BACK_BUTTON_TAG).performClick()
            assertTrue(backClicked)
        }

    @Test
    fun whenFavoriteButtonClicked_callsOnFavoriteClick(): Unit =
        with(composeTestRule) {
            var favoriteClicked = false
            val movie = sampleMovie(1)
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {
                        favoriteClicked = true
                    },
                )
            }

            onNodeWithTag(MOVIE_DETAIL_FAVORITE_FAB_TAG).performClick()
            assertTrue(favoriteClicked)
        }

    @Test
    fun whenMovieIsFavorite_showFilledFavoriteIcon(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1).copy(favorite = true)
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_FAVORITE_FAB_TAG).assertExists()
        }

    @Test
    fun whenMovieIsNotFavorite_showEmptyFavoriteIcon(): Unit =
        with(composeTestRule) {
            val movie = sampleMovie(1).copy(favorite = false)
            setContent {
                MovieDetailScreen(
                    state =
                        MovieDetailUiState(
                            movie = movie,
                            isLoadingMovie = false,
                        ),
                    onBack = {},
                    onFavoriteClick = {},
                )
            }

            onNodeWithTag(MOVIE_DETAIL_FAVORITE_FAB_TAG).assertExists()
        }
}
