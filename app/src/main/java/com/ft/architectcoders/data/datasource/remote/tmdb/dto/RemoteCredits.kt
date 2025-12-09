package com.ft.architectcoders.data.datasource.remote.tmdb.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteCredits(
    val id: Int,
    val cast: List<RemoteCast>,
    val crew: List<RemoteCrew>
)

@Serializable
data class RemoteCast(
    val id: Int,
    val name: String,
    val character: String,
    @SerialName("profile_path") val profilePath: String?,
    val order: Int
)

@Serializable
data class RemoteCrew(
    val id: Int,
    val name: String,
    val job: String,
    @SerialName("profile_path") val profilePath: String?
)