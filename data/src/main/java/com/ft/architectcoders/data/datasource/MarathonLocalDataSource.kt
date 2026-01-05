package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.model.MarathonHistoryItem
import com.ft.architectcoders.domain.model.MarathonPlan
import kotlinx.coroutines.flow.Flow

interface MarathonLocalDataSource {
    fun getMarathonHistory(): Flow<List<MarathonHistoryItem>>
    suspend fun getMarathonById(marathonId: Long): MarathonPlan?
    suspend fun saveMarathon(plan: MarathonPlan): Long
    suspend fun deleteMarathon(marathonId: Long)
}

