package com.ft.architectcoders.ui.screens.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.ChallengeMovieOption
import com.ft.architectcoders.domain.model.ChallengeStatus
import com.ft.architectcoders.domain.model.DailyChallenge
import com.ft.architectcoders.domain.model.DecadeCount
import com.ft.architectcoders.domain.model.GenreCount
import com.ft.architectcoders.domain.model.UserChallengeSignals
import com.ft.architectcoders.usecases.FetchMoviesUseCase
import com.ft.architectcoders.usecases.challenge.CompleteDailyChallengeUseCase
import com.ft.architectcoders.usecases.challenge.GetOrCreateDailyChallengeUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface DailyChallengeUiState {
    data object Loading : DailyChallengeUiState
    data class Success(
        val challenge: DailyChallenge,
        val isCompleting: Boolean = false,
        val showCompletionCelebration: Boolean = false,
    ) : DailyChallengeUiState
    data class Error(val message: String) : DailyChallengeUiState
}

sealed interface DailyChallengeEvent {
    data class NavigateToMovieDetail(val movieId: Int) : DailyChallengeEvent
    data class ChallengeCompleted(val badgeName: String, val badgeEmoji: String) : DailyChallengeEvent
    data class ChallengeFailed(val correctMovies: List<String>) : DailyChallengeEvent
    data class ShowError(val message: String) : DailyChallengeEvent
}

class DailyChallengeViewModel(
    private val getOrCreateDailyChallengeUseCase: GetOrCreateDailyChallengeUseCase,
    private val completeDailyChallengeUseCase: CompleteDailyChallengeUseCase,
    private val fetchMoviesUseCase: FetchMoviesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<DailyChallengeUiState>(DailyChallengeUiState.Loading)
    val state: StateFlow<DailyChallengeUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<DailyChallengeEvent>()
    val events: SharedFlow<DailyChallengeEvent> = _events.asSharedFlow()

    private val todayDate: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        loadChallenge()
    }

    fun loadChallenge() {
        viewModelScope.launch {
            _state.value = DailyChallengeUiState.Loading

            val signals = buildUserSignals()

            when (val result = getOrCreateDailyChallengeUseCase(todayDate, signals)) {
                is Result.Success -> {
                    _state.value = DailyChallengeUiState.Success(
                        challenge = result.data,
                        showCompletionCelebration = false,
                    )
                }
                is Result.Error -> {
                    _state.value = DailyChallengeUiState.Error(result.error.message)
                }
                is Result.Loading -> {
                }
            }
        }
    }

    fun onMovieClick(movie: ChallengeMovieOption) {
        viewModelScope.launch {
            _events.emit(DailyChallengeEvent.NavigateToMovieDetail(movie.movieId))
        }
    }

    fun onCompleteChallenge(movie: ChallengeMovieOption) {
        val currentState = _state.value
        if (currentState !is DailyChallengeUiState.Success) return
        if (currentState.challenge.status != ChallengeStatus.ACTIVE) return

        viewModelScope.launch {
            _state.value = currentState.copy(isCompleting = true)

            when (val result = completeDailyChallengeUseCase(todayDate, movie.movieId)) {
                is Result.Success -> {
                    val updatedChallenge = result.data
                    val wasSuccessful = updatedChallenge.status == ChallengeStatus.COMPLETED

                    _state.value = DailyChallengeUiState.Success(
                        challenge = updatedChallenge,
                        isCompleting = false,
                        showCompletionCelebration = wasSuccessful,
                    )

                    if (wasSuccessful) {
                        _events.emit(
                            DailyChallengeEvent.ChallengeCompleted(
                                badgeName = updatedChallenge.badge.name,
                                badgeEmoji = updatedChallenge.badge.emoji,
                            ),
                        )
                    } else {
                        val correctMovies = currentState.challenge.movieOptions
                            .filter { it.isCorrectChoice }
                            .map { it.title }
                        _events.emit(DailyChallengeEvent.ChallengeFailed(correctMovies))
                    }
                }
                is Result.Error -> {
                    _state.value = currentState.copy(isCompleting = false)
                    _events.emit(DailyChallengeEvent.ShowError(result.error.message))
                }
                is Result.Loading -> {

                }
            }
        }
    }

    fun onDismissCelebration() {
        val currentState = _state.value
        if (currentState is DailyChallengeUiState.Success) {
            _state.value = currentState.copy(showCompletionCelebration = false)
        }
    }

    fun onRetry() {
        loadChallenge()
    }

    private suspend fun buildUserSignals(): UserChallengeSignals {
        return try {
            val movies = fetchMoviesUseCase().first()
            val favoriteMovies = movies.filter { it.favorite }

            val topGenres = listOf(
                GenreCount(28, "Action", favoriteMovies.size / 3),
                GenreCount(18, "Drama", favoriteMovies.size / 4),
                GenreCount(35, "Comedy", favoriteMovies.size / 5),
            ).filter { it.count > 0 }

            val decadeHistogram = favoriteMovies
                .mapNotNull { movie ->
                    movie.releaseDate.take(4).toIntOrNull()?.let { year ->
                        (year / 10) * 10
                    }
                }
                .groupingBy { it }
                .eachCount()
                .map { (decade, count) -> DecadeCount(decade, count) }
                .sortedByDescending { it.count }

            UserChallengeSignals(
                topGenres = topGenres,
                decadeHistogram = decadeHistogram,
                favoriteMovieIds = favoriteMovies.map { it.id },
            )
        } catch (e: Exception) {
            UserChallengeSignals(
                topGenres = emptyList(),
                decadeHistogram = emptyList(),
                favoriteMovieIds = emptyList(),
            )
        }
    }
}

