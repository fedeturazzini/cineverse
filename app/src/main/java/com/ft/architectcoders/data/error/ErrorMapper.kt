package com.ft.architectcoders.data.error

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
        return when (throwable) {
            is HttpException -> {
                val code = throwable.code()
                AppError.GeminiApiError(
                    message = when (code) {
                        400 -> "Solicitud inválida a Gemini"
                        401 -> "API key de Gemini inválida"
                        403 -> "Acceso prohibido a Gemini"
                        429 -> "Límite de solicitudes de Gemini excedido"
                        500 -> "Error del servidor de Gemini"
                        else -> "Error de Gemini: $code"
                    },
                    cause = throwable,
                    reason = throwable.message()
                )
            }
            else -> {
                val isGeminiError = throwable.javaClass.name.contains("gemini", ignoreCase = true) ||
                                   throwable.message?.contains("gemini", ignoreCase = true) == true ||
                                   throwable.message?.contains("generative", ignoreCase = true) == true
                
                if (isGeminiError) {
                    AppError.GeminiApiError(
                        message = throwable.message ?: "Error de la API de Gemini",
                        cause = throwable,
                        reason = throwable.message
                    )
                } else {
                    when (throwable) {
                        is UnknownHostException -> AppError.NoInternetConnection(
                            source = ErrorSource.GEMINI_API,
                            cause = throwable
                        )
                        is SocketTimeoutException -> AppError.Timeout(
                            source = ErrorSource.GEMINI_API,
                            cause = throwable
                        )
                        is IOException -> AppError.NoInternetConnection(
                            message = "Error de conexión con Gemini",
                            source = ErrorSource.GEMINI_API,
                            cause = throwable
                        )
                        else -> AppError.GeminiApiError(
                            message = throwable.message ?: "Error desconocido de Gemini",
                            cause = throwable
                        )
                    }
                }
            }
        }
    }
    
    private fun mapToAppError(throwable: Throwable, source: ErrorSource): AppError {
        return when (throwable) {
            is HttpException -> {
                val code = throwable.code()
                AppError.HttpError(
                    code = code,
                    message = when (code) {
                        400 -> "Solicitud inválida"
                        401 -> "No autorizado"
                        403 -> "Acceso prohibido"
                        404 -> "Recurso no encontrado"
                        in 500..599 -> "Error del servidor"
                        else -> "Error HTTP $code"
                    },
                    cause = throwable,
                    source = source
                )
            }
            is UnknownHostException -> AppError.NoInternetConnection(
                source = source,
                cause = throwable
            )
            is SocketTimeoutException -> AppError.Timeout(
                source = source,
                cause = throwable
            )
            is IOException -> AppError.NoInternetConnection(
                message = "Error de conexión",
                source = source,
                cause = throwable
            )
            is SerializationException -> AppError.ParsingError(
                source = source,
                cause = throwable
            )
            is AppError -> throwable
            else -> AppError.UnknownError(
                message = throwable.message ?: "Error desconocido",
                source = source,
                cause = throwable
            )
        }
    }
}

fun <T> Throwable.toTmdbResult(): com.ft.architectcoders.Result<T> = 
    com.ft.architectcoders.Result.Error(ErrorMapper.mapTmdbError(this))

fun <T> Throwable.toGeminiResult(): com.ft.architectcoders.Result<T> = 
    com.ft.architectcoders.Result.Error(ErrorMapper.mapGeminiError(this))

fun <T> Throwable.toResult(): com.ft.architectcoders.Result<T> = 
    com.ft.architectcoders.Result.Error(ErrorMapper.mapTmdbError(this))

