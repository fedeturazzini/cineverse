package com.ft.architectcoders.framework.remote.tmdb.mapper

import com.ft.architectcoders.framework.remote.tmdb.dto.RemoteVideo
import com.ft.architectcoders.domain.model.MovieVideo

fun RemoteVideo.toDomain() =
    MovieVideo(
        id = id,
        key = key,
        name = name,
        site = site,
        type = type,
        thumbnailUrl = "https://img.youtube.com/vi/$key/hqdefault.jpg",
    )
