package com.ft.architectcoders.framework

import android.annotation.SuppressLint
import android.location.Location
import com.ft.architectcoders.data.datasource.LocationDataSource
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationDataSourceImpl(private val fusedLocationClient: FusedLocationProviderClient) :
    LocationDataSource {
    override suspend fun findLastLocation() = fusedLocationClient.lastLocation()

    @SuppressLint("MissingPermission")
    private suspend fun FusedLocationProviderClient.lastLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            lastLocation.addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }
}
