package com.ft.architectcoders.framework.remote.tmdb.mapper

import com.ft.architectcoders.framework.remote.tmdb.dto.RemoteCast
import com.ft.architectcoders.domain.model.Cast

fun RemoteCast.toDomain() =
    Cast(
        id = id,
        name = name,
        character = character,
        profilePhoto = profilePath?.let { "https://image.tmdb.org/t/p/w185/$it" },
    )
