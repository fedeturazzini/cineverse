package com.ft.architectcoders.data.repository.region

import com.ft.architectcoders.data.datasource.RegionDataSource

class RegionRepositoryImpl(private val regionDataSource: RegionDataSource) : RegionRepository {
    override suspend fun findLastRegion(): String = regionDataSource.findLastRegion()
}
