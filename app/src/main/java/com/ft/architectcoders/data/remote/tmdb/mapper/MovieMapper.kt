package com.ft.architectcoders.data.remote.tmdb.mapper

import com.ft.architectcoders.data.remote.tmdb.dto.RemoteMovie
import com.ft.architectcoders.domain.model.Movie

fun RemoteMovie.toDomain() =
    Movie(
        id = id,
        title = title,
        poster = "https://image.tmdb.org/t/p/w185/$posterPath",
        overview = overview,
    )
