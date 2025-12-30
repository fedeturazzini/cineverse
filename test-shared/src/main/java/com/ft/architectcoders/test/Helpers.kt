package com.ft.architectcoders.test

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.error.ErrorSource
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.Cast
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.MovieVideo

fun sampleMovie(id: Int) = Movie (
    id = id,
    title = "Movie $id",
    originalTitle = "Original Movie $id",
    overview = "Overview $id",
    releaseDate = "2023-01-01",
    favorite = false,
    aiRating = 2.7F,
    poster = "",
    backdrop = "",
    aiQuote = "Excellent",
)

fun sampleMovies (vararg ids: Int) = ids.map { sampleMovie(it) }

fun sampleCast(id: Int) = Cast(
    id = id,
    name = "Actor $id",
    character = "Character $id",
    profilePhoto = "photo_$id.jpg"
)

fun sampleMovieVideo(id: String) = MovieVideo(
    id = id,
    key = "video_key_$id",
    name = "Video $id",
    site = "YouTube",
    type = "Trailer",
    thumbnailUrl = "thumbnail_$id.jpg"
)

fun sampleAiReview(
    rating: Float = 4.5f,
    quote: String = "Una película excelente"
) = AiReview(
    rating = rating,
    quote = quote
)

// Result helpers
fun <T> successResult(data: T): Result.Success<T> = Result.Success(data)

fun errorResult(error: AppError): Result.Error = Result.Error(error)

fun loadingResult(): Result.Loading = Result.Loading

// AppError helpers
fun sampleHttpError(
    code: Int = 404,
    message: String = "Not Found",
    source: ErrorSource = ErrorSource.TMDB_API
): AppError.HttpError = AppError.HttpError(
    code = code,
    message = message,
    source = source
)

fun sampleUnknownError(
    message: String = "Test error",
    source: ErrorSource = ErrorSource.UNKNOWN
): AppError.UnknownError = AppError.UnknownError(
    message = message,
    source = source
)

