package com.ft.architectcoders.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.data.repository.gemini.GeminiRepository
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.Cast
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.MovieVideo
import com.ft.architectcoders.usecases.FindMovieByIdUseCase
import com.ft.architectcoders.usecases.GetMovieCreditsUseCase
import com.ft.architectcoders.usecases.GetMovieVideosUseCase
import com.ft.architectcoders.usecases.ToggleFavoriteMovieUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val movie: Movie? = null,
    val cast: List<Cast> = emptyList(),
    val videos: List<MovieVideo> = emptyList(),
    val isLoadingMovie: Boolean = false,
    val isLoadingAiReview: Boolean = false,
    val message: String? = null,
    val aiReviewState: AiReviewUiState = AiReviewUiState.NotRequested,
    val error: MovieDetailError? = null,
)

sealed interface AiReviewUiState {
    data object Loading : AiReviewUiState

    data class Success(val aiReview: AiReview) : AiReviewUiState

    data class Error(val message: String) : AiReviewUiState

    data object NotRequested : AiReviewUiState
}

private fun Result<AiReview>.toAiReviewUiState(): AiReviewUiState {
    return when (this) {
        is Result.Success -> AiReviewUiState.Success(this.data)
        is Result.Error -> AiReviewUiState.Error(this.error.message)
        is Result.Loading -> AiReviewUiState.Loading
    }
}

data class MovieDetailError(
    val genericError: String? = null,
    val aiError: String? = null,
    val castError: String? = null,
    val videosError: String? = null,
)

class MovieDetailViewModel(
    private val movieId: Int,
    private val findMovieByIdUseCase: FindMovieByIdUseCase,
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase,
    private val geminiRepository: GeminiRepository,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<MovieDetailUiState> =
        findMovieByIdUseCase(movieId)
            .flatMapLatest { movieResult ->
                when (movieResult) {
                    is Result.Success -> {
                        combine(
                            getMovieCreditsUseCase(movieId),
                            geminiRepository.getMovieReview(
                                movieId,
                                movieResult.data.title,
                                movieResult.data.overview,
                            ),
                            getMovieVideosUseCase(movieId),
                        ) { castResult, aiReview, videosResult ->
                            val cast =
                                when (castResult) {
                                    is Result.Success -> castResult.data
                                    else -> emptyList()
                                }
                            val videos =
                                when (videosResult) {
                                    is Result.Success -> videosResult.data
                                    else -> emptyList()
                                }
                            val castError = (castResult as? Result.Error)?.error?.message
                            val videosError = (videosResult as? Result.Error)?.error?.message

                            val error =
                                if (castError != null || videosError != null) {
                                    MovieDetailError(castError = castError, videosError = videosError)
                                } else {
                                    null
                                }

                            MovieDetailUiState(
                                movie = movieResult.data,
                                cast = cast,
                                videos = videos,
                                isLoadingMovie = false,
                                aiReviewState = aiReview.toAiReviewUiState(),
                                error = error,
                            )
                        }
                    }
                    is Result.Error ->
                        flowOf(
                            MovieDetailUiState(
                                isLoadingMovie = false,
                                error = MovieDetailError(genericError = movieResult.error.message),
                            ),
                        )
                    is Result.Loading ->
                        flowOf(MovieDetailUiState(isLoadingMovie = true))
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MovieDetailUiState(isLoadingMovie = true),
            )

    fun onFavoriteClick() {
        state.value.movie?.let { movie ->
            viewModelScope.launch {
                toggleFavoriteMovieUseCase(movie)
            }
        }
    }
}
