package com.ft.architectcoders.ui.screens.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.MoodProfile
import com.ft.architectcoders.domain.model.MoodVector
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.usecases.mood.BuildMoodProfileUseCase
import com.ft.architectcoders.usecases.mood.GetMoodRecommendationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MoodRadarUiState {
    val moodVector: MoodVector

    data class Idle(
        override val moodVector: MoodVector = MoodVector(),
    ) : MoodRadarUiState

    data class Loading(
        override val moodVector: MoodVector,
    ) : MoodRadarUiState

    data class Success(
        override val moodVector: MoodVector,
        val profile: MoodProfile,
        val movies: List<Movie>,
    ) : MoodRadarUiState

    data class Error(
        override val moodVector: MoodVector,
        val message: String,
    ) : MoodRadarUiState
}

enum class MoodAxis {
    ENERGY,
    HUMOR,
    TENSION,
    ROMANCE,
    CEREBRAL,
}

class MoodRadarViewModel(
    private val buildMoodProfileUseCase: BuildMoodProfileUseCase,
    private val getMoodRecommendationsUseCase: GetMoodRecommendationsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<MoodRadarUiState>(MoodRadarUiState.Idle())
    val state: StateFlow<MoodRadarUiState> = _state.asStateFlow()

    fun onMoodChange(
        axis: MoodAxis,
        value: Int,
    ) {
        val currentVector = _state.value.moodVector
        val newVector =
            when (axis) {
                MoodAxis.ENERGY -> currentVector.copy(energy = value.coerceIn(0, 100))
                MoodAxis.HUMOR -> currentVector.copy(humor = value.coerceIn(0, 100))
                MoodAxis.TENSION -> currentVector.copy(tension = value.coerceIn(0, 100))
                MoodAxis.ROMANCE -> currentVector.copy(romance = value.coerceIn(0, 100))
                MoodAxis.CEREBRAL -> currentVector.copy(cerebral = value.coerceIn(0, 100))
            }

        _state.value =
            when (val current = _state.value) {
                is MoodRadarUiState.Idle -> current.copy(moodVector = newVector)
                is MoodRadarUiState.Success -> MoodRadarUiState.Idle(moodVector = newVector)
                is MoodRadarUiState.Error -> MoodRadarUiState.Idle(moodVector = newVector)
                is MoodRadarUiState.Loading -> current
            }
    }

    fun onBuildNight() {
        val moodVector = _state.value.moodVector
        _state.value = MoodRadarUiState.Loading(moodVector)

        viewModelScope.launch {
            when (val profileResult = buildMoodProfileUseCase(moodVector)) {
                is Result.Success -> {
                    when (val recsResult = getMoodRecommendationsUseCase(profileResult.data)) {
                        is Result.Success -> {
                            _state.value =
                                MoodRadarUiState.Success(
                                    moodVector = moodVector,
                                    profile = recsResult.data.profile,
                                    movies = recsResult.data.movies,
                                )
                        }
                        is Result.Error -> {
                            _state.value =
                                MoodRadarUiState.Error(
                                    moodVector = moodVector,
                                    message = recsResult.error.message,
                                )
                        }
                        is Result.Loading -> {}
                    }
                }
                is Result.Error -> {
                    _state.value =
                        MoodRadarUiState.Error(
                            moodVector = moodVector,
                            message = profileResult.error.message,
                        )
                }
                is Result.Loading -> {
                }
            }
        }
    }

    fun onRetry() {
        onBuildNight()
    }
}
