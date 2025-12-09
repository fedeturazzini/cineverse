package com.ft.architectcoders.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.Result
import com.ft.architectcoders.data.repository.gemini.GeminiRepository
import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.domain.error.asNullable
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.Cast
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.MovieVideo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val movie: Movie? = null,
    val cast: List<Cast> = emptyList(),
    val videos: List<MovieVideo> = emptyList(),
    val isLoadingMovie: Boolean = false,
    val isLoadingAiReview: Boolean = false,
    val message: String? = null,
    val aiReview: AiReview? = null,
    val error: MovieDetailError? = null,
)

data class MovieDetailError(
    val genericError: String? = null,
    val aiError: String? = null,
)

class MovieDetailViewModel(
    private val movieId: Int,
    private val movieRepository: MovieRepository,
    private val geminiRepository: GeminiRepository,
) : ViewModel() {
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<MovieDetailUiState> =
        movieRepository.findMovieById(movieId)
            .flatMapLatest { movieResult ->
                when (movieResult) {
                    is Result.Success -> combine(
                        movieRepository.findMovieById(movieId),
                        movieRepository.getMovieCredits(movieId),
                        geminiRepository.getMovieReview(movieId, movieResult.data.title, movieResult.data.overview).asNullable(),
                        movieRepository.getMovieVideos(movieId),
                    ) { currentMovieResult, cast, aiReview, movieVideos ->
                        when (currentMovieResult) {
                            is Result.Success -> MovieDetailUiState(
                                movie = currentMovieResult.data,
                                cast = cast,
                                videos = movieVideos,
                                isLoadingMovie = false,
                                aiReview = aiReview,
                                error =
                                    if (aiReview == null) {
                                        MovieDetailError(aiError = "No se pudo generar la reseña con IA")
                                    } else {
                                        null
                                    },
                            )
                            is Result.Error -> MovieDetailUiState(
                                isLoadingMovie = false,
                                error = MovieDetailError(genericError = currentMovieResult.error.message)
                            )
                            is Result.Loading -> MovieDetailUiState(isLoadingMovie = true)
                        }
                    }
                    is Result.Error -> flowOf(
                        MovieDetailUiState(
                            isLoadingMovie = false,
                            error = MovieDetailError(genericError = movieResult.error.message)
                        )
                    )
                    is Result.Loading -> flowOf(
                        MovieDetailUiState(isLoadingMovie = true)
                    )
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
                movieRepository.toggleFavorite(movie)
            }
        }
    }
}
