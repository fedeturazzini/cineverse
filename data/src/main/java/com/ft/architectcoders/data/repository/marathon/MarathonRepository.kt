package com.ft.architectcoders.data.repository.marathon

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.MarathonHistoryItem
import com.ft.architectcoders.domain.model.MarathonPlan
import com.ft.architectcoders.domain.model.MarathonTheme
import com.ft.architectcoders.domain.model.MarathonThemeId
import kotlinx.coroutines.flow.Flow

interface MarathonRepository {
    fun getThemes(): List<MarathonTheme>
    suspend fun generatePlan(themeId: MarathonThemeId): Result<MarathonPlan>
    suspend fun savePlan(plan: MarathonPlan): Long
    fun getHistory(): Flow<List<MarathonHistoryItem>>
    suspend fun getMarathonById(marathonId: Long): MarathonPlan?
}

