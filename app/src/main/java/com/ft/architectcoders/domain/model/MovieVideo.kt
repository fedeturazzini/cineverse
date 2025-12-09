package com.ft.architectcoders.domain.model

data class MovieVideo(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val thumbnailUrl: String
) {
    val isYouTube: Boolean
        get() = site.equals("YouTube", ignoreCase = true)

    val isTrailer: Boolean
        get() = type.equals("Trailer", ignoreCase = true)

    val youtubeUrl: String
        get() = "https://www.youtube.com/watch?v=$key"
}