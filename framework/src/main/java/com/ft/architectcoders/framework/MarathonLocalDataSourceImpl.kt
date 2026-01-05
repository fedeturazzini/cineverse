package com.ft.architectcoders.framework

import com.ft.architectcoders.data.datasource.MarathonLocalDataSource
import com.ft.architectcoders.domain.model.MarathonHistoryItem
import com.ft.architectcoders.domain.model.MarathonPick
import com.ft.architectcoders.domain.model.MarathonPlan
import com.ft.architectcoders.domain.model.MarathonThemeId
import com.ft.architectcoders.framework.database.DbMarathon
import com.ft.architectcoders.framework.database.DbMarathonPick
import com.ft.architectcoders.framework.database.MarathonDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MarathonLocalDataSourceImpl(
    private val marathonDao: MarathonDao,
) : MarathonLocalDataSource {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getMarathonHistory(): Flow<List<MarathonHistoryItem>> {
        return marathonDao.getAllMarathons().map { marathons ->
            marathons.map { dbMarathon ->
                val pickCount = marathonDao.getPicksCount(dbMarathon.id)
                MarathonHistoryItem(
                    id = dbMarathon.id,
                    themeId = MarathonThemeId.valueOf(dbMarathon.themeId),
                    themeTitle = dbMarathon.themeTitle,
                    timestamp = dbMarathon.timestamp,
                    movieCount = pickCount,
                )
            }
        }
    }

    override suspend fun getMarathonById(marathonId: Long): MarathonPlan? {
        val dbMarathon = marathonDao.getMarathonById(marathonId) ?: return null
        val dbPicks = marathonDao.getPicksByMarathonId(marathonId)

        return MarathonPlan(
            id = dbMarathon.id,
            themeId = MarathonThemeId.valueOf(dbMarathon.themeId),
            themeTitle = dbMarathon.themeTitle,
            tagline = dbMarathon.tagline,
            picks = dbPicks.map { it.toDomain() },
            generatedByAi = dbMarathon.generatedByAi,
            timestamp = dbMarathon.timestamp,
        )
    }

    override suspend fun saveMarathon(plan: MarathonPlan): Long {
        val dbMarathon = DbMarathon(
            themeId = plan.themeId.name,
            themeTitle = plan.themeTitle,
            tagline = plan.tagline,
            generatedByAi = plan.generatedByAi,
            timestamp = plan.timestamp,
        )
        val dbPicks = plan.picks.map { it.toDb(0) }
        return marathonDao.saveMarathonWithPicks(dbMarathon, dbPicks)
    }

    override suspend fun deleteMarathon(marathonId: Long) {
        marathonDao.deleteMarathon(marathonId)
    }

    private fun DbMarathonPick.toDomain(): MarathonPick {
        val warningsList = try {
            json.decodeFromString<List<String>>(warnings)
        } catch (e: Exception) {
            emptyList()
        }
        return MarathonPick(
            movieId = movieId,
            movieTitle = movieTitle,
            moviePoster = moviePoster,
            movieYear = movieYear,
            movieRating = movieRating,
            order = order,
            why = why,
            warnings = warningsList,
        )
    }

    private fun MarathonPick.toDb(marathonId: Long): DbMarathonPick {
        return DbMarathonPick(
            marathonId = marathonId,
            movieId = movieId,
            movieTitle = movieTitle,
            moviePoster = moviePoster,
            movieYear = movieYear,
            movieRating = movieRating,
            order = order,
            why = why,
            warnings = json.encodeToString(warnings),
        )
    }
}

