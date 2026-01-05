package com.ft.architectcoders.ui.screens.marathon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.MarathonPlan
import com.ft.architectcoders.domain.model.MarathonThemeId
import com.ft.architectcoders.usecases.marathon.GenerateMarathonPlanUseCase
import com.ft.architectcoders.usecases.marathon.SaveMarathonUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MarathonTodayUiState {
    data object Loading : MarathonTodayUiState
    data class Success(
        val plan: MarathonPlan,
        val isSaving: Boolean = false,
        val isSaved: Boolean = false,
    ) : MarathonTodayUiState
    data class Error(val message: String) : MarathonTodayUiState
    data object Empty : MarathonTodayUiState
}

sealed interface MarathonTodayEvent {
    data class SaveSuccess(val marathonId: Long) : MarathonTodayEvent
    data class SaveError(val message: String) : MarathonTodayEvent
}

class MarathonTodayViewModel(
    private val themeId: MarathonThemeId,
    private val generateMarathonPlanUseCase: GenerateMarathonPlanUseCase,
    private val saveMarathonUseCase: SaveMarathonUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MarathonTodayUiState>(MarathonTodayUiState.Loading)
    val state: StateFlow<MarathonTodayUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<MarathonTodayEvent>()
    val events: SharedFlow<MarathonTodayEvent> = _events.asSharedFlow()

    init {
        generatePlan()
    }

    fun generatePlan() {
        viewModelScope.launch {
            _state.value = MarathonTodayUiState.Loading

            when (val result = generateMarathonPlanUseCase(themeId)) {
                is Result.Success -> {
                    val plan = result.data
                    if (plan.picks.isEmpty()) {
                        _state.value = MarathonTodayUiState.Empty
                    } else {
                        _state.value = MarathonTodayUiState.Success(plan)
                    }
                }
                is Result.Error -> {
                    _state.value = MarathonTodayUiState.Error(result.error.message)
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    fun onRegenerate() {
        generatePlan()
    }

    fun onSave() {
        val currentState = _state.value
        if (currentState !is MarathonTodayUiState.Success) return
        if (currentState.isSaved) return

        viewModelScope.launch {
            _state.value = currentState.copy(isSaving = true)

            try {
                val marathonId = saveMarathonUseCase(currentState.plan)
                _state.value = currentState.copy(isSaving = false, isSaved = true)
                _events.emit(MarathonTodayEvent.SaveSuccess(marathonId))
            } catch (e: Exception) {
                _state.value = currentState.copy(isSaving = false)
                _events.emit(MarathonTodayEvent.SaveError(e.message ?: "Error al guardar"))
            }
        }
    }
}

