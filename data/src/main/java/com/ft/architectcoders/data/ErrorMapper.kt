package com.ft.architectcoders.data

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.error.ErrorSource
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

object ErrorMapper {
    fun mapTmdbError(throwable: Throwable): AppError {
        return mapToAppError(throwable, ErrorSource.TMDB_API)
    }

    fun mapGeminiError(throwable: Throwable): AppError {
        val message = throwable.message ?: ""
        val isQuotaError = message.contains("quota", ignoreCase = true) ||
            message.contains("exceeded", ignoreCase = true) ||
            message.contains("rate limit", ignoreCase = true)

        if (isQuotaError) {
            val retrySeconds = extractRetrySeconds(message)
            return AppError.QuotaExceeded(
                cause = throwable,
                retryAfterSeconds = retrySeconds,
            )
        }

        return when (throwable) {
            is HttpException -> {
                val code = throwable.code()
                if (code == 429) {
                    AppError.QuotaExceeded(cause = throwable)
                } else {
                    AppError.GeminiApiError(
                        message = when (code) {
                            400 -> "Solicitud inválida a Gemini"
                            401 -> "API key de Gemini inválida"
                            403 -> "Acceso prohibido a Gemini"
                            500 -> "Error del servidor de Gemini"
                            else -> "Error de Gemini: $code"
                        },
                        cause = throwable,
                        reason = throwable.message(),
                    )
                }
            }
            else -> {
                val isGeminiError =
                    throwable.javaClass.name.contains("gemini", ignoreCase = true) ||
                        message.contains("gemini", ignoreCase = true) ||
                        message.contains("generative", ignoreCase = true)

                if (isGeminiError) {
                    AppError.GeminiApiError(
                        message = message.ifEmpty { "Error de la API de Gemini" },
                        cause = throwable,
                        reason = message,
                    )
                } else {
                    when (throwable) {
                        is UnknownHostException ->
                            AppError.NoInternetConnection(
                                source = ErrorSource.GEMINI_API,
                                cause = throwable,
                            )
                        is SocketTimeoutException ->
                            AppError.Timeout(
                                source = ErrorSource.GEMINI_API,
                                cause = throwable,
                            )
                        is IOException ->
                            AppError.NoInternetConnection(
                                message = "Error de conexión con Gemini",
                                source = ErrorSource.GEMINI_API,
                                cause = throwable,
                            )
                        else ->
                            AppError.GeminiApiError(
                                message = message.ifEmpty { "Error desconocido de Gemini" },
                                cause = throwable,
                            )
                    }
                }
            }
        }
    }

    private fun extractRetrySeconds(message: String): Int? {
        val regex = Regex("""retry in (\d+)""", RegexOption.IGNORE_CASE)
        return regex.find(message)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun mapToAppError(
        throwable: Throwable,
        source: ErrorSource,
    ): AppError {
        return when (throwable) {
            is HttpException -> {
                val code = throwable.code()
                AppError.HttpError(
                    code = code,
                    message =
                        when (code) {
                            400 -> "Solicitud inválida"
                            401 -> "No autorizado"
                            403 -> "Acceso prohibido"
                            404 -> "Recurso no encontrado"
                            in 500..599 -> "Error del servidor"
                            else -> "Error HTTP $code"
                        },
                    cause = throwable,
                    source = source,
                )
            }
            is UnknownHostException ->
                AppError.NoInternetConnection(
                    source = source,
                    cause = throwable,
                )
            is SocketTimeoutException ->
                AppError.Timeout(
                    source = source,
                    cause = throwable,
                )
            is IOException ->
                AppError.NoInternetConnection(
                    message = "Error de conexión",
                    source = source,
                    cause = throwable,
                )
            is SerializationException ->
                AppError.ParsingError(
                    source = source,
                    cause = throwable,
                )
            is AppError -> throwable
            else ->
                AppError.UnknownError(
                    message = throwable.message ?: "Error desconocido",
                    source = source,
                    cause = throwable,
                )
        }
    }
}

fun <T> Throwable.toTmdbResult(): Result<T> = Result.Error(ErrorMapper.mapTmdbError(this))

fun <T> Throwable.toGeminiResult(): Result<T> = Result.Error(ErrorMapper.mapGeminiError(this))

fun <T> Throwable.toResult(): Result<T> = Result.Error(ErrorMapper.mapTmdbError(this))
