package com.ft.architectcoders.ui.screens.marathon

import androidx.lifecycle.ViewModel
import com.ft.architectcoders.domain.model.MarathonTheme
import com.ft.architectcoders.domain.model.MarathonThemeId
import com.ft.architectcoders.usecases.marathon.GetMarathonThemesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MarathonPickerUiState(
    val themes: List<MarathonTheme> = emptyList(),
    val selectedThemeId: MarathonThemeId? = null,
)

class MarathonPickerViewModel(
    getMarathonThemesUseCase: GetMarathonThemesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        MarathonPickerUiState(themes = getMarathonThemesUseCase())
    )
    val state: StateFlow<MarathonPickerUiState> = _state.asStateFlow()

    fun onThemeSelected(themeId: MarathonThemeId) {
        _state.value = _state.value.copy(selectedThemeId = themeId)
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedThemeId = null)
    }
}

