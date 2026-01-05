package com.ft.architectcoders.ui.screens.wrap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.CineverseWrap
import com.ft.architectcoders.usecases.wrap.CineverseWrapResult
import com.ft.architectcoders.usecases.wrap.GetCineverseWrapUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CineverseWrapUiState {
    data object Loading : CineverseWrapUiState

    data class Success(
        val wrap: CineverseWrap,
    ) : CineverseWrapUiState

    data class Empty(
        val interactionsNeeded: Int,
    ) : CineverseWrapUiState

    data class Error(
        val message: String,
    ) : CineverseWrapUiState
}

sealed interface CineverseWrapEvent {
    data class ShareText(val text: String) : CineverseWrapEvent
    data class ShowError(val message: String) : CineverseWrapEvent
}

class CineverseWrapViewModel(
    private val getCineverseWrapUseCase: GetCineverseWrapUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<CineverseWrapUiState>(CineverseWrapUiState.Loading)
    val state: StateFlow<CineverseWrapUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CineverseWrapEvent>()
    val events: SharedFlow<CineverseWrapEvent> = _events.asSharedFlow()

    init {
        loadWrap()
    }

    fun onShareClick() {
        val currentState = _state.value
        if (currentState is CineverseWrapUiState.Success) {
            val wrap = currentState.wrap
            val shareText = buildShareText(wrap)
            viewModelScope.launch {
                _events.emit(CineverseWrapEvent.ShareText(shareText))
            }
        }
    }

    fun onRetry() {
        loadWrap()
    }

    private fun loadWrap() {
        _state.value = CineverseWrapUiState.Loading

        viewModelScope.launch {
            when (val result = getCineverseWrapUseCase()) {
                is Result.Success -> {
                    when (val wrapResult = result.data) {
                        is CineverseWrapResult.Success -> {
                            _state.value = CineverseWrapUiState.Success(
                                wrap = wrapResult.wrap,
                            )
                        }
                        is CineverseWrapResult.Empty -> {
                            _state.value = CineverseWrapUiState.Empty(
                                interactionsNeeded = wrapResult.interactionsNeeded,
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _state.value = CineverseWrapUiState.Error(
                        message = result.error.message,
                    )
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    private fun buildShareText(wrap: CineverseWrap): String {
        return buildString {
            append(wrap.funnyProfileSummary)
            append("\n\n")
            append(wrap.shareText)
        }
    }
}
