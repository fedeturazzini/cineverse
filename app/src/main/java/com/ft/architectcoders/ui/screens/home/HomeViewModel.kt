package com.ft.architectcoders.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val region: String = "",
)

class HomeViewModel(
    private val movieRepository: MovieRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun init(region: String) = loadMovies(region)

    private fun loadMovies(region: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, region = region) }

            try {
                val movies = movieRepository.fetchPopularMovies(region)

                _state.update {
                    it.copy(
                        isLoading = false,
                        movies = movies,
                    )
                }
            } catch (e: Exception) {
                // Todo: manejar errores mas adelante
            }
        }
    }
}
