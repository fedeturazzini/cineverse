package com.ft.architectcoders.ui.screens.marathon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.domain.model.MarathonPlan
import com.ft.architectcoders.usecases.marathon.GetMarathonByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MarathonDetailUiState {
    data object Loading : MarathonDetailUiState
    data class Success(val plan: MarathonPlan) : MarathonDetailUiState
    data object NotFound : MarathonDetailUiState
}

class MarathonDetailViewModel(
    private val marathonId: Long,
    private val getMarathonByIdUseCase: GetMarathonByIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MarathonDetailUiState>(MarathonDetailUiState.Loading)
    val state: StateFlow<MarathonDetailUiState> = _state.asStateFlow()

    init {
        loadMarathon()
    }

    private fun loadMarathon() {
        viewModelScope.launch {
            val plan = getMarathonByIdUseCase(marathonId)
            _state.value = if (plan != null) {
                MarathonDetailUiState.Success(plan)
            } else {
                MarathonDetailUiState.NotFound
            }
        }
    }
}

