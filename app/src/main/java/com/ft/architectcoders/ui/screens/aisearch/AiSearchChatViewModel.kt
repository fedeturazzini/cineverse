package com.ft.architectcoders.ui.screens.aisearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.model.AiSearchSession
import com.ft.architectcoders.domain.model.AiSearchUserSignals
import com.ft.architectcoders.domain.model.ChatMessage
import com.ft.architectcoders.domain.model.ChatRole
import com.ft.architectcoders.domain.model.DetectedPreferences
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.usecases.FetchMoviesUseCase
import com.ft.architectcoders.usecases.aisearch.SaveAiSearchSessionUseCase
import com.ft.architectcoders.usecases.aisearch.SendAiSearchMessageUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface AiSearchChatUiState {
    data object Loading : AiSearchChatUiState
    data class Chatting(
        val messages: List<ChatMessage>,
        val questionsUsed: Int,
        val isProcessing: Boolean,
        val currentResults: List<Movie>,
    ) : AiSearchChatUiState
    data class Completed(
        val messages: List<ChatMessage>,
        val finalBullets: List<String>,
        val finalResults: List<Movie>,
    ) : AiSearchChatUiState
    data class Error(val message: String) : AiSearchChatUiState
}

sealed interface AiSearchChatEvent {
    data class NavigateToMovieDetail(val movieId: Int) : AiSearchChatEvent
    data class ShowError(val message: String) : AiSearchChatEvent
}

class AiSearchChatViewModel(
    private val sendAiSearchMessageUseCase: SendAiSearchMessageUseCase,
    private val saveAiSearchSessionUseCase: SaveAiSearchSessionUseCase,
    private val fetchMoviesUseCase: FetchMoviesUseCase,
) : ViewModel() {

    companion object {
        const val MAX_QUESTIONS = 3
        private const val INITIAL_MESSAGE = "¿Qué necesitás hoy? Tenés solo 3 preguntas."
        private const val OUT_OF_SCOPE_MESSAGE = "Solo puedo ayudarte a elegir películas. Decime qué tipo de peli querés ver."
    }

    private val _state = MutableStateFlow<AiSearchChatUiState>(AiSearchChatUiState.Loading)
    val state: StateFlow<AiSearchChatUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AiSearchChatEvent>()
    val events: SharedFlow<AiSearchChatEvent> = _events.asSharedFlow()

    private var currentSession: AiSearchSession = createNewSession()
    private var allResults: MutableList<Movie> = mutableListOf()
    private var currentPreferences: DetectedPreferences? = null
    private var finalBullets: List<String> = emptyList()

    init {
        startNewSession()
    }

    private fun startNewSession() {
        currentSession = createNewSession()
        allResults.clear()
        currentPreferences = null
        finalBullets = emptyList()

        _state.value = AiSearchChatUiState.Chatting(
            messages = currentSession.messages,
            questionsUsed = 0,
            isProcessing = false,
            currentResults = emptyList(),
        )
    }

    private fun createNewSession(): AiSearchSession {
        val initialMessage = ChatMessage(
            role = ChatRole.ASSISTANT,
            content = INITIAL_MESSAGE,
        )
        return AiSearchSession(
            id = 0,
            createdAt = System.currentTimeMillis(),
            messages = listOf(initialMessage),
            detectedPreferences = null,
            recommendedMovieIds = emptyList(),
            finalBullets = emptyList(),
            isCompleted = false,
        )
    }

    fun onUserSend(text: String) {
        val currentState = _state.value
        if (currentState !is AiSearchChatUiState.Chatting) return
        if (currentState.isProcessing) return
        if (text.isBlank()) return

        val questionsUsed = currentState.questionsUsed
        if (questionsUsed >= MAX_QUESTIONS) return

        viewModelScope.launch {
            _state.value = currentState.copy(isProcessing = true)

            val userSignals = buildUserSignals()

            when (val result = sendAiSearchMessageUseCase(currentSession, text, userSignals)) {
                is Result.Success -> {
                    val turnResult = result.data

                    if (turnResult.isOutOfScope) {
                        val assistantResponse = ChatMessage(
                            role = ChatRole.ASSISTANT,
                            content = OUT_OF_SCOPE_MESSAGE,
                        )
                        val updatedMessages = currentSession.messages +
                            ChatMessage(role = ChatRole.USER, content = text) +
                            assistantResponse

                        currentSession = currentSession.copy(messages = updatedMessages)

                        _state.value = currentState.copy(
                            messages = updatedMessages,
                            isProcessing = false,
                        )
                        return@launch
                    }

                    val userMessage = ChatMessage(role = ChatRole.USER, content = text)
                    val assistantMessage = ChatMessage(
                        role = ChatRole.ASSISTANT,
                        content = turnResult.assistantMessage,
                        movieResults = turnResult.movies,
                    )

                    val updatedMessages = currentSession.messages + userMessage + assistantMessage
                    val newQuestionsUsed = questionsUsed + 1

                    allResults.addAll(turnResult.movies)
                    turnResult.detectedPreferences?.let { currentPreferences = it }
                    turnResult.finalBullets?.let { finalBullets = it }

                    currentSession = currentSession.copy(
                        messages = updatedMessages,
                        detectedPreferences = currentPreferences,
                        recommendedMovieIds = allResults.map { it.id }.distinct(),
                    )

                    if (newQuestionsUsed >= MAX_QUESTIONS) {
                        completeSession()
                    } else {
                        _state.value = AiSearchChatUiState.Chatting(
                            messages = updatedMessages,
                            questionsUsed = newQuestionsUsed,
                            isProcessing = false,
                            currentResults = turnResult.movies,
                        )
                    }
                }
                is Result.Error -> {
                    val errorMessage = when (val error = result.error) {
                        is AppError.QuotaExceeded -> {
                            val retryText = error.retryAfterSeconds?.let { " Intentá en $it segundos." } ?: ""
                            "Se agotaron los tokens de IA.$retryText"
                        }
                        else -> error.message
                    }

                    val assistantErrorMessage = ChatMessage(
                        role = ChatRole.ASSISTANT,
                        content = errorMessage,
                    )
                    val updatedMessages = currentSession.messages +
                        ChatMessage(role = ChatRole.USER, content = text) +
                        assistantErrorMessage
                    currentSession = currentSession.copy(messages = updatedMessages)

                    _state.value = currentState.copy(
                        messages = updatedMessages,
                        isProcessing = false,
                    )
                }
                is Result.Loading -> {}
            }
        }
    }

    private suspend fun completeSession() {
        val completedSession = currentSession.copy(
            isCompleted = true,
            finalBullets = finalBullets,
        )
        currentSession = completedSession

        saveAiSearchSessionUseCase(completedSession)

        _state.value = AiSearchChatUiState.Completed(
            messages = completedSession.messages,
            finalBullets = finalBullets,
            finalResults = allResults.distinctBy { it.id },
        )
    }

    fun onMovieClick(movieId: Int) {
        viewModelScope.launch {
            _events.emit(AiSearchChatEvent.NavigateToMovieDetail(movieId))
        }
    }

    fun onRestart() {
        startNewSession()
    }

    private suspend fun buildUserSignals(): AiSearchUserSignals {
        return try {
            val movies = fetchMoviesUseCase().first()
            val favoriteMovies = movies.filter { it.favorite }

            AiSearchUserSignals(
                region = "AR",
                topGenreIds = listOf(28, 18, 35),
                avoidMovieIds = favoriteMovies.map { it.id },
            )
        } catch (e: Exception) {
            AiSearchUserSignals(
                region = "AR",
                topGenreIds = emptyList(),
                avoidMovieIds = emptyList(),
            )
        }
    }
}

