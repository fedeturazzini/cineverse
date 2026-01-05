package com.ft.architectcoders.framework

import android.location.Geocoder
import com.ft.architectcoders.data.datasource.DEFAULT_REGION
import com.ft.architectcoders.data.datasource.LocationDataSource
import com.ft.architectcoders.data.datasource.RegionDataSource
import com.ft.architectcoders.domain.CineVerseLocation

class RegionDataSourceImpl(
    private val geocoder: Geocoder,
    private val locationDataSource: LocationDataSource,
) : RegionDataSource {
    override suspend fun findLastRegion(): String = locationDataSource.findLastLocation()?.toRegion() ?: DEFAULT_REGION

    override suspend fun CineVerseLocation.toRegion(): String {
        val addresses = geocoder.getFromLocationCompat(latitude, longitude, 1)
        val region = addresses.firstOrNull()?.countryCode
        return region ?: DEFAULT_REGION
    }
}
