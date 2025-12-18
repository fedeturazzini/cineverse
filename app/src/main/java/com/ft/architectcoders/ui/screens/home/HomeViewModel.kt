package com.ft.architectcoders.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.Result
import com.ft.architectcoders.data.error.ErrorMapper
import com.ft.architectcoders.domain.error.ErrorSource
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.data.repository.region.RegionRepository
import com.ft.architectcoders.usecases.FetchMoviesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class UiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val region: String = "",
    val error: String? = null,
)

class HomeViewModel(
    fetchMoviesUseCase: FetchMoviesUseCase,
    private val regionRepository: RegionRepository,
) : ViewModel() {
    private val permissionGranted = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<UiState> =
        permissionGranted
            .filter { it }
            .flatMapLatest {
                fetchMoviesUseCase()
                    .map<List<Movie>, Result<List<Movie>>> { movies ->
                        Result.Success(movies)
                    }
                    .catch { e ->
                        emit(Result.Error(ErrorMapper.mapTmdbError(e)))
                    }
            }
            .map { result ->
                when (result) {
                    is Result.Success ->
                        UiState(
                            movies = result.data,
                            region = regionRepository.findLastRegion(),
                            isLoading = false,
                            error = null,
                        )
                    is Result.Error ->
                        UiState(
                            isLoading = false,
                            error = result.error.message,
                        )
                    is Result.Loading -> UiState(isLoading = true)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState(isLoading = true),
            )

    fun permissionGranted() {
        permissionGranted.value = true
    }
}
