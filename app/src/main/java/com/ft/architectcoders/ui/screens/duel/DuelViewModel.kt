package com.ft.architectcoders.ui.screens.duel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.DuelChoice
import com.ft.architectcoders.domain.model.DuelSession
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.TasteFingerprint
import com.ft.architectcoders.usecases.duel.CompleteDuelSessionUseCase
import com.ft.architectcoders.usecases.duel.GetDuelCandidatesUseCase
import com.ft.architectcoders.usecases.duel.SubmitDuelChoiceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DuelUiState {
    data object Loading : DuelUiState

    data class InProgress(
        val movieA: Movie,
        val movieB: Movie,
        val currentRound: Int,
        val totalRounds: Int = 10,
    ) : DuelUiState

    data object GeneratingFingerprint : DuelUiState

    data class Completed(
        val fingerprint: TasteFingerprint,
    ) : DuelUiState

    data class Error(val message: String) : DuelUiState
}

class DuelViewModel(
    private val getDuelCandidatesUseCase: GetDuelCandidatesUseCase,
    private val submitDuelChoiceUseCase: SubmitDuelChoiceUseCase,
    private val completeDuelSessionUseCase: CompleteDuelSessionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<DuelUiState>(DuelUiState.Loading)
    val state: StateFlow<DuelUiState> = _state.asStateFlow()

    private var candidates: List<Movie> = emptyList()
    private var currentSession = DuelSession()
    private var currentPairIndex = 0

    init {
        loadCandidates()
    }

    fun loadCandidates() {
        viewModelScope.launch {
            _state.value = DuelUiState.Loading
            currentSession = DuelSession()
            currentPairIndex = 0

            when (val result = getDuelCandidatesUseCase()) {
                is Result.Success -> {
                    candidates = result.data
                    if (candidates.size >= 2) {
                        showNextPair()
                    } else {
                        _state.value = DuelUiState.Error("No hay suficientes películas disponibles")
                    }
                }
                is Result.Error -> {
                    _state.value = DuelUiState.Error(result.error.message)
                }
                is Result.Loading -> {
                    _state.value = DuelUiState.Loading
                }
            }
        }
    }

    fun onChooseA() {
        val currentState = _state.value
        if (currentState is DuelUiState.InProgress) {
            submitChoice(
                winner = currentState.movieA,
                loser = currentState.movieB,
                round = currentState.currentRound,
            )
        }
    }

    fun onChooseB() {
        val currentState = _state.value
        if (currentState is DuelUiState.InProgress) {
            submitChoice(
                winner = currentState.movieB,
                loser = currentState.movieA,
                round = currentState.currentRound,
            )
        }
    }

    private fun submitChoice(winner: Movie, loser: Movie, round: Int) {
        val choice = DuelChoice(
            winnerId = winner.id,
            loserId = loser.id,
            winnerTitle = winner.title,
            loserTitle = loser.title,
            roundNumber = round,
        )

        currentSession = submitDuelChoiceUseCase(currentSession, choice)
        currentPairIndex++

        if (currentSession.choices.size >= TOTAL_ROUNDS) {
            completeSession()
        } else {
            showNextPair()
        }
    }

    private fun showNextPair() {
        val indexA = currentPairIndex * 2
        val indexB = indexA + 1

        if (indexB < candidates.size) {
            _state.value = DuelUiState.InProgress(
                movieA = candidates[indexA],
                movieB = candidates[indexB],
                currentRound = currentSession.choices.size + 1,
            )
        } else {
            _state.value = DuelUiState.Error("No hay más películas disponibles")
        }
    }

    private fun completeSession() {
        viewModelScope.launch {
            _state.value = DuelUiState.GeneratingFingerprint

            when (val result = completeDuelSessionUseCase(currentSession)) {
                is Result.Success -> {
                    _state.value = DuelUiState.Completed(result.data)
                }
                is Result.Error -> {
                    _state.value = DuelUiState.Error(result.error.message)
                }
                is Result.Loading -> {
                    _state.value = DuelUiState.GeneratingFingerprint
                }
            }
        }
    }

    fun onRetry() {
        loadCandidates()
    }

    companion object {
        const val TOTAL_ROUNDS = 10
    }
}

