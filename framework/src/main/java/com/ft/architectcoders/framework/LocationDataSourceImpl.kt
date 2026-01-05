package com.ft.architectcoders.framework

import android.annotation.SuppressLint
import com.ft.architectcoders.data.datasource.LocationDataSource
import com.ft.architectcoders.domain.CineVerseLocation
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationDataSourceImpl(private val fusedLocationClient: FusedLocationProviderClient) :
    LocationDataSource {
    override suspend fun findLastLocation() = fusedLocationClient.lastLocation()

    @SuppressLint("MissingPermission")
    private suspend fun FusedLocationProviderClient.lastLocation(): CineVerseLocation? {
        return suspendCancellableCoroutine { continuation ->
            lastLocation.addOnSuccessListener { location ->
                val cineVerseLocation =
                    location?.let {
                        CineVerseLocation(it.latitude, it.longitude)
                    }

                continuation.resume(cineVerseLocation)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }
}
