package com.ft.architectcoders.domain.model

data class Profile(
    val id: Int = 1,
    val name: String = "",
    val profilePhotoPath: String? = null,
    val region: String = "US",
    val favoriteGenres: List<String> = emptyList(),
)
