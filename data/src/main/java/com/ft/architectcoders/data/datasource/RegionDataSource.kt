package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.CineVerseLocation

interface RegionDataSource {
    suspend fun findLastRegion(): String

    suspend fun CineVerseLocation.toRegion(): String
}

const val DEFAULT_REGION = "US"
