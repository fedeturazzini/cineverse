package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.CineVerseLocation

interface LocationDataSource {
    suspend fun findLastLocation(): CineVerseLocation?
}
