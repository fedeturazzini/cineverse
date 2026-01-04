package com.ft.architectcoders.framework

import com.ft.architectcoders.data.datasource.DuelLocalDataSource
import com.ft.architectcoders.domain.model.DuelChoice
import com.ft.architectcoders.domain.model.DuelSession
import com.ft.architectcoders.domain.model.TasteFingerprint
import com.ft.architectcoders.framework.database.DbDuelChoice
import com.ft.architectcoders.framework.database.DbDuelSession
import com.ft.architectcoders.framework.database.DbTasteFingerprint
import com.ft.architectcoders.framework.database.DuelDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DuelLocalDataSourceImpl(
    private val duelDao: DuelDao,
) : DuelLocalDataSource {

    override suspend fun saveDuelSession(session: DuelSession): Long {
        val dbSession = DbDuelSession(
            id = session.id,
            timestamp = session.timestamp,
            isCompleted = session.isCompleted,
        )
        val dbChoices = session.choices.map { it.toDbDuelChoice(session.id) }

        return duelDao.saveDuelSessionWithChoices(dbSession, dbChoices)
    }

    override suspend fun saveTasteFingerprint(fingerprint: TasteFingerprint) {
        val dbFingerprint = DbTasteFingerprint(
            id = fingerprint.id,
            sessionId = fingerprint.sessionId,
            timestamp = fingerprint.timestamp,
            insights = fingerprint.insights.joinToString(SEPARATOR),
            dominantTraits = fingerprint.dominantTraits.joinToString(SEPARATOR),
            generatedByAi = fingerprint.generatedByAi,
        )
        duelDao.insertFingerprint(dbFingerprint)
    }

    override fun getLastFingerprint(): Flow<TasteFingerprint?> {
        return duelDao.getLastFingerprint().map { it?.toDomainFingerprint() }
    }

    override fun getDuelHistory(): Flow<List<DuelSession>> {
        return duelDao.getAllSessions().map { sessions ->
            sessions.map { dbSession ->
                val choices = duelDao.getChoicesBySessionId(dbSession.id)
                dbSession.toDomainSession(choices)
            }
        }
    }

    override suspend fun markSessionCompleted(sessionId: Long) {
        duelDao.markSessionCompleted(sessionId)
    }

    private fun DuelChoice.toDbDuelChoice(sessionId: Long) = DbDuelChoice(
        sessionId = sessionId,
        winnerId = winnerId,
        loserId = loserId,
        winnerTitle = winnerTitle,
        loserTitle = loserTitle,
        roundNumber = roundNumber,
    )

    private fun DbDuelSession.toDomainSession(choices: List<DbDuelChoice>) = DuelSession(
        id = id,
        timestamp = timestamp,
        choices = choices.map { it.toDomainChoice() },
        isCompleted = isCompleted,
    )

    private fun DbDuelChoice.toDomainChoice() = DuelChoice(
        winnerId = winnerId,
        loserId = loserId,
        winnerTitle = winnerTitle,
        loserTitle = loserTitle,
        roundNumber = roundNumber,
    )

    private fun DbTasteFingerprint.toDomainFingerprint() = TasteFingerprint(
        id = id,
        sessionId = sessionId,
        timestamp = timestamp,
        insights = insights.split(SEPARATOR).filter { it.isNotBlank() },
        dominantTraits = dominantTraits.split(SEPARATOR).filter { it.isNotBlank() },
        generatedByAi = generatedByAi,
    )

    companion object {
        private const val SEPARATOR = "|||"
    }
}

