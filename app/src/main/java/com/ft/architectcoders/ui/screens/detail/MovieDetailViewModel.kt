package com.ft.architectcoders.ui.screens.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.data.remote.gemini.GeminiAiServiceImpl
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val movie: Movie? = null,
    val isLoadingMovie: Boolean = false,
    val isLoadingAiReview: Boolean = false,
    val message: String? = null,
    val aiReview: AiReview? = null,
    val error: String? = null,
)

sealed interface MovieDetailUiEvent {
    data class ShowMessage(val message: String) : MovieDetailUiEvent
}

class MovieDetailViewModel(
    private val movieId: Int,
    private val movieRepository: MovieRepository,
    private val geminiAiServiceImpl: GeminiAiServiceImpl,
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailUiState())
    val state: StateFlow<MovieDetailUiState> = _state.asStateFlow()

    private val _event = MutableStateFlow<MovieDetailUiEvent?>(null)


    init {
        loadMovie()
    }

    private fun loadMovie() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMovie = true)
            try {
                val movie = movieRepository.findMovieById(movieId)
                _state.value = _state.value.copy(movie = movie, isLoadingMovie = false)

                loadAiReview(movie)
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(
                        isLoadingMovie = false,
                        error = e.message,
                    )
            }
        }
    }

    private fun loadAiReview(movie: Movie) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingAiReview = true)
            try {
                val review =
                    geminiAiServiceImpl.generateMovieReview(
                        title = movie.title,
                        overview = movie.overview,
                    )
                _state.value =
                    _state.value.copy(
                        aiReview = review,
                        isLoadingAiReview = false,
                    )
            } catch (e: Exception) {
                Log.e("Ai review fallo: ", e.message.toString())
            } finally {
                _state.value = _state.value.copy(isLoadingAiReview = false)
            }
        }
    }

    fun onFavoriteClick() {
        _state.update {
            it.copy(message = "Agregaste la pelicula favoritos")
        }
    }

    fun onMessageShown() {
        _state.update {
            it.copy(message = null)
        }
    }
}
