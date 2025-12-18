package com.ft.architectcoders.domain.error

enum class ErrorSource {
    TMDB_API,
    GEMINI_API,
    LOCAL_DB,
    UNKNOWN,
}

sealed interface AppError {
    val message: String
    val cause: Throwable?
    val source: ErrorSource

    data class NoInternetConnection(
        override val message: String = "Sin conexión a internet",
        override val cause: Throwable? = null,
        override val source: ErrorSource = ErrorSource.UNKNOWN,
    ) : AppError

    data class Timeout(
        override val message: String = "Tiempo de espera agotado",
        override val cause: Throwable? = null,
        override val source: ErrorSource = ErrorSource.UNKNOWN,
    ) : AppError

    data class HttpError(
        val code: Int,
        override val message: String,
        override val cause: Throwable? = null,
        override val source: ErrorSource = ErrorSource.UNKNOWN,
    ) : AppError

    data class ParsingError(
        override val message: String = "Error al procesar la respuesta",
        override val cause: Throwable? = null,
        override val source: ErrorSource = ErrorSource.UNKNOWN,
    ) : AppError

    data class GeminiApiError(
        override val message: String,
        override val cause: Throwable? = null,
        val reason: String? = null,
    ) : AppError {
        override val source: ErrorSource = ErrorSource.GEMINI_API
    }

    data class UnknownError(
        override val message: String = "Error desconocido",
        override val cause: Throwable? = null,
        override val source: ErrorSource = ErrorSource.UNKNOWN,
    ) : AppError
}
