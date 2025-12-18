package com.ft.architectcoders

import com.ft.architectcoders.domain.error.AppError

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>

    data class Error(val error: AppError) : Result<Nothing>

    data object Loading : Result<Nothing>
}
