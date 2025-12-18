package com.ft.architectcoders.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ft.architectcoders.Result
import com.ft.architectcoders.data.error.toResult
import com.ft.architectcoders.data.repository.profile.ProfileRepository
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.ui.common.photo.FileStorageHelper
import com.ft.architectcoders.usecases.FetchMoviesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiFlags(
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val showPhotoDialog: Boolean = false
)

data class ProfileState(
    val name: String = "",
    val profilePhotoPath: String? = null,
    val region: String = "US",
    val favoriteMovies: List<Movie> = emptyList(),
    val selectedGenres: List<String> = emptyList(),
    val uiFlags: ProfileUiFlags = ProfileUiFlags()
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    fetchMoviesUseCase: FetchMoviesUseCase,
) : ViewModel() {
    private val _uiFlags = MutableStateFlow(ProfileUiFlags(isLoading = true))
    private val _localName = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<ProfileState> = combine(
        profileRepository.profile,
        fetchMoviesUseCase().map { movies ->
            movies.filter { it.favorite }
        },
        _uiFlags,
        _localName
    ) { profile, favoriteMovies, uiFlags, localName ->
        ProfileState(
            name = localName ?: profile.name,
            profilePhotoPath = profile.profilePhotoPath,
            region = profile.region,
            selectedGenres = profile.favoriteGenres,
            favoriteMovies = favoriteMovies,
            uiFlags = uiFlags
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileState(uiFlags = ProfileUiFlags(isLoading = true))
        )

    fun onNameChanged(name: String) {
        _localName.value = name

        viewModelScope.launch {
            try {
                val currentProfile = profileRepository.profile.first()
                profileRepository.saveProfile(
                    currentProfile.copy(name = name)
                )
                _uiFlags.update { it.copy(error = null) }
            } catch (e: Exception) {
                _uiFlags.update { currentFlags ->
                    val errorMessage = when (val result = e.toResult<Nothing>()) {
                        is Result.Error -> result.error.message
                        else -> null
                    }
                    currentFlags.copy(error = errorMessage)
                }
            }
        }
    }

    // TODO: Optimizar en el futuro
//    fun onRegionChanged(region: String) {
//        viewModelScope.launch {
//            try {
//                val currentProfile = profileRepository.profile.first()
//                profileRepository.saveProfile(
//                    currentProfile.copy(region = region)
//                )
//                errorFlow.value = null
//            } catch (e: Exception) {
//                errorFlow.value = when (val result = e.toResult<Nothing>()) {
//                    is Result.Error -> when (result.error.source) {
//                        ErrorSource.LOCAL_DB -> "Error al guardar la región"
//                        else -> result.error.message
//                    }
//                    else -> "Error desconocido"
//                }
//            }
//        }
//    }

    fun onGenreToggled(genreId: String) {
        viewModelScope.launch {
            try {
                val currentProfile = profileRepository.profile.first()
                val currentGenres = currentProfile.favoriteGenres.toMutableList()
                if (currentGenres.contains(genreId)) {
                    currentGenres.remove(genreId)
                } else {
                    currentGenres.add(genreId)
                }
                profileRepository.saveProfile(
                    currentProfile.copy(favoriteGenres = currentGenres)
                )
                _uiFlags.update { it.copy(error = null) }
            } catch (e: Exception) {
                _uiFlags.update { currentFlags ->
                    val errorMessage = when (val result = e.toResult<Nothing>()) {
                        is Result.Error -> result.error.message
                        else -> null
                    }
                    currentFlags.copy(error = errorMessage)
                }
            }
        }
    }

    fun onImageSelected(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiFlags.update { it.copy(isLoading = true, error = null) }
            try {
                val imagePath = FileStorageHelper.saveProfileImage(context, uri)
                val currentProfile = profileRepository.profile.first()
                profileRepository.saveProfile(
                    currentProfile.copy(profilePhotoPath = imagePath)
                )
            } catch (e: Exception) {
                _uiFlags.update { currentFlags ->
                    val errorMessage = when (val result = e.toResult<Nothing>()) {
                        is Result.Error -> result.error.message
                        else -> null
                    }
                    currentFlags.copy(error = errorMessage, isLoading = false)
                }
            } finally {
                _uiFlags.update { it.copy(isLoading = false) }
            }
        }
    }

    fun toggleEditMode() {
        _uiFlags.update { it.copy(isEditing = !it.isEditing) }
    }

    fun showPhotoDialog() {
        _uiFlags.update { it.copy(showPhotoDialog = true) }
    }

    fun hidePhotoDialog() {
        _uiFlags.update { it.copy(showPhotoDialog = false) }
    }

    fun saveProfile() {
        _localName.value = null
        _uiFlags.update { it.copy(isEditing = false) }
    }

    fun clearError() {
        _uiFlags.update { it.copy(error = null) }
    }
}