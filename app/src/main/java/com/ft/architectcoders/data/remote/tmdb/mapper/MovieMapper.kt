package com.ft.architectcoders.data.remote.tmdb.mapper

import com.ft.architectcoders.data.remote.tmdb.dto.RemoteMovie
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
    )
