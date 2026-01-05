package com.ft.architectcoders.framework

import com.ft.architectcoders.data.datasource.AiSearchLocalDataSource
import com.ft.architectcoders.domain.model.AiSearchSession
import com.ft.architectcoders.domain.model.ChatMessage
import com.ft.architectcoders.domain.model.ChatRole
import com.ft.architectcoders.domain.model.DetectedPreferences
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.framework.database.AiSearchDao
import com.ft.architectcoders.framework.database.DbAiSearchSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AiSearchLocalDataSourceImpl(
    private val aiSearchDao: AiSearchDao,
    private val json: Json,
) : AiSearchLocalDataSource {

    override suspend fun saveSession(session: AiSearchSession): Long {
        val dbSession = session.toDb()
        return if (session.id == 0L) {
            aiSearchDao.insertSession(dbSession)
        } else {
            aiSearchDao.updateSession(dbSession)
            session.id
        }
    }

    override suspend fun getSessionById(id: Long): AiSearchSession? {
        return aiSearchDao.getSessionById(id)?.toDomain()
    }

    override fun getAllSessions(): Flow<List<AiSearchSession>> {
        return aiSearchDao.getAllSessions().map { list -> list.map { it.toDomain() } }
    }

    private fun AiSearchSession.toDb(): DbAiSearchSession {
        val messagesSerializable = messages.map { msg ->
            SerializableChatMessage(
                role = msg.role.name,
                content = msg.content,
                timestamp = msg.timestamp,
                movieIds = msg.movieResults.map { it.id },
            )
        }
        val prefsSerializable = detectedPreferences?.let {
            SerializablePreferences(
                includeGenreIds = it.includeGenreIds,
                excludeGenreIds = it.excludeGenreIds,
                yearFrom = it.yearFrom,
                yearTo = it.yearTo,
                maxRuntimeMinutes = it.maxRuntimeMinutes,
                minVoteAverage = it.minVoteAverage,
                keywords = it.keywords,
                similarToTitles = it.similarToTitles,
            )
        }
        return DbAiSearchSession(
            id = id,
            createdAt = createdAt,
            messagesJson = json.encodeToString(messagesSerializable),
            preferencesJson = prefsSerializable?.let { json.encodeToString(it) },
            recommendedMovieIds = json.encodeToString(recommendedMovieIds),
            finalBulletsJson = if (finalBullets.isNotEmpty()) json.encodeToString(finalBullets) else null,
            isCompleted = isCompleted,
        )
    }

    private fun DbAiSearchSession.toDomain(): AiSearchSession {
        val messagesSerializable: List<SerializableChatMessage> = json.decodeFromString(messagesJson)
        val messages = messagesSerializable.map { msg ->
            ChatMessage(
                role = ChatRole.valueOf(msg.role),
                content = msg.content,
                timestamp = msg.timestamp,
                movieResults = emptyList(),
            )
        }
        val prefs = preferencesJson?.let {
            val p: SerializablePreferences = json.decodeFromString(it)
            DetectedPreferences(
                includeGenreIds = p.includeGenreIds,
                excludeGenreIds = p.excludeGenreIds,
                yearFrom = p.yearFrom,
                yearTo = p.yearTo,
                maxRuntimeMinutes = p.maxRuntimeMinutes,
                minVoteAverage = p.minVoteAverage,
                keywords = p.keywords,
                similarToTitles = p.similarToTitles,
            )
        }
        val movieIds: List<Int> = json.decodeFromString(recommendedMovieIds)
        val bullets: List<String> = finalBulletsJson?.let { json.decodeFromString(it) } ?: emptyList()
        return AiSearchSession(
            id = id,
            createdAt = createdAt,
            messages = messages,
            detectedPreferences = prefs,
            recommendedMovieIds = movieIds,
            finalBullets = bullets,
            isCompleted = isCompleted,
        )
    }

    @Serializable
    private data class SerializableChatMessage(
        val role: String,
        val content: String,
        val timestamp: Long,
        val movieIds: List<Int>,
    )

    @Serializable
    private data class SerializablePreferences(
        val includeGenreIds: List<Int>,
        val excludeGenreIds: List<Int>,
        val yearFrom: Int?,
        val yearTo: Int?,
        val maxRuntimeMinutes: Int?,
        val minVoteAverage: Float?,
        val keywords: List<String>,
        val similarToTitles: List<String>,
    )
}

