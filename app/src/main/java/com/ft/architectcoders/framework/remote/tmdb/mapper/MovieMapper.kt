package com.ft.architectcoders.framework.remote.tmdb.mapper

import com.ft.architectcoders.framework.remote.tmdb.dto.RemoteMovie
import com.ft.architectcoders.domain.model.Movie

fun RemoteMovie.toDomain() =
    Movie(
        id = id,
        title = title,
        originalTitle = originalTitle,
        poster = "https://image.tmdb.org/t/p/w185/$posterPath",
        releaseDate = releaseDate,
        backdrop = backdropPath?.let { "https://image.tmdb.org/t/p/w780/$it" },
        overview = overview,
        favorite = false,
        aiRating = null,
        aiQuote = null,
    )
