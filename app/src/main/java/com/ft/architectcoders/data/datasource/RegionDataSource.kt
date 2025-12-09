package com.ft.architectcoders.data.datasource

import android.app.Application
import android.location.Geocoder
import android.location.Location
import com.ft.architectcoders.ui.common.getFromLocationCompat

interface RegionDataSource {
    suspend fun findLastRegion(): String
}

const val DEFAULT_REGION = "US"

class RegionDataSourceImpl(
    application: Application,
    private val locationDataSource: LocationDataSource,
) : RegionDataSource {
    val geocoder = Geocoder(application)

    override suspend fun findLastRegion(): String = locationDataSource.findLastLocation()?.toRegion() ?: DEFAULT_REGION

    private suspend fun Location.toRegion(): String {
        val addresses = geocoder.getFromLocationCompat(latitude, longitude, 1)
        val region = addresses.firstOrNull()?.countryCode
        return region ?: DEFAULT_REGION
    }
}
