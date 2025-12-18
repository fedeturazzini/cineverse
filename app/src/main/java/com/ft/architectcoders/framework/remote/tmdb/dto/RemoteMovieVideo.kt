package com.ft.architectcoders.framework.remote.tmdb.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteVideos(
    val id: Int,
    val results: List<RemoteVideo>,
)

@Serializable
data class RemoteVideo(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val official: Boolean,
    @SerialName("published_at") val publishedAt: String,
    @SerialName("iso_639_1") val iso6391: String,
    @SerialName("iso_3166_1") val iso31661: String,
)
