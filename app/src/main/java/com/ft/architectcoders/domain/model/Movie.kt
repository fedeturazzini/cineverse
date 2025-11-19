package com.ft.architectcoders.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val poster: String,
    val backdrop: String?,
    val releaseDate: String,
    val overview: String,
)
