package com.ft.architectcoders.data.repository.region

interface RegionRepository {
    suspend fun findLastRegion(): String
}
