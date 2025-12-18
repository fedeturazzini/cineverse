package com.ft.architectcoders.data.datasource

import android.location.Location

interface RegionDataSource {
    suspend fun findLastRegion(): String
    suspend fun Location.toRegion(): String
}

const val DEFAULT_REGION = "US"
