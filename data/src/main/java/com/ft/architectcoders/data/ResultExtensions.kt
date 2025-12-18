package com.ft.architectcoders.data

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.data.ErrorMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.asResult(): Flow<Result<T>> =
    this
        .map<T, Result<T>> { Result.Success(it) }
        .catch { emit(Result.Error(ErrorMapper.mapTmdbError(it))) }
        .onStart { emit(Result.Loading) }

fun <T> Flow<Result<T>>.asNullable(): Flow<T?> =
    this.map { result ->
        when (result) {
            is Result.Success -> result.data
            else -> null
        }
    }
