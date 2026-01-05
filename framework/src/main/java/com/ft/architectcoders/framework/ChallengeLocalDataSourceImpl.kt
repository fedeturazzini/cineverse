package com.ft.architectcoders.framework

import com.ft.architectcoders.data.datasource.ChallengeLocalDataSource
import com.ft.architectcoders.domain.model.ChallengeBadge
import com.ft.architectcoders.domain.model.ChallengeMovieOption
import com.ft.architectcoders.domain.model.ChallengeStatus
import com.ft.architectcoders.domain.model.ChallengeType
import com.ft.architectcoders.domain.model.DailyChallenge
import com.ft.architectcoders.framework.database.ChallengeDao
import com.ft.architectcoders.framework.database.DbChallengeBadge
import com.ft.architectcoders.framework.database.DbDailyChallenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChallengeLocalDataSourceImpl(
    private val challengeDao: ChallengeDao,
) : ChallengeLocalDataSource {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getChallengeByDate(date: String): DailyChallenge? {
        return challengeDao.getChallengeByDate(date)?.toDomain()
    }

    override suspend fun saveChallenge(challenge: DailyChallenge) {
        challengeDao.saveChallenge(challenge.toDb())
    }

    override suspend fun completeChallenge(
        date: String,
        movieId: Int,
    ) {
        val timestamp = System.currentTimeMillis()
        challengeDao.completeChallenge(date, movieId, timestamp)

        // Also save the badge as unlocked
        val challenge = challengeDao.getChallengeByDate(date)
        if (challenge != null) {
            challengeDao.saveBadge(
                DbChallengeBadge(
                    id = challenge.badgeId,
                    name = challenge.badgeName,
                    emoji = challenge.badgeEmoji,
                    unlockedAt = timestamp,
                ),
            )
        }
    }

    override fun getChallengeHistory(): Flow<List<DailyChallenge>> {
        return challengeDao.getChallengeHistory().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getUnlockedBadges(): Flow<List<ChallengeBadge>> {
        return challengeDao.getUnlockedBadges().map { list ->
            list.map { it.toDomain() }
        }
    }

    private fun DbDailyChallenge.toDomain(): DailyChallenge {
        val movieOptions = try {
            json.decodeFromString<List<MovieOptionJson>>(movieOptionsJson)
                .map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }

        val rulesList = try {
            json.decodeFromString<List<String>>(rules)
        } catch (e: Exception) {
            emptyList()
        }

        return DailyChallenge(
            date = date,
            type = ChallengeType.valueOf(type),
            title = title,
            reason = reason,
            rules = rulesList,
            badge = ChallengeBadge(
                id = badgeId,
                name = badgeName,
                emoji = badgeEmoji,
            ),
            movieOptions = movieOptions,
            status = ChallengeStatus.valueOf(status),
            completedMovieId = completedMovieId,
            completedAt = completedAt,
            generatedByAi = generatedByAi,
        )
    }

    private fun DailyChallenge.toDb(): DbDailyChallenge {
        val movieOptionsJson = json.encodeToString(
            movieOptions.map { it.toJson() },
        )
        val rulesJson = json.encodeToString(rules)

        return DbDailyChallenge(
            date = date,
            type = type.name,
            title = title,
            reason = reason,
            rules = rulesJson,
            badgeId = badge.id,
            badgeName = badge.name,
            badgeEmoji = badge.emoji,
            movieOptionsJson = movieOptionsJson,
            status = status.name,
            completedMovieId = completedMovieId,
            completedAt = completedAt,
            generatedByAi = generatedByAi,
        )
    }

    private fun DbChallengeBadge.toDomain(): ChallengeBadge {
        return ChallengeBadge(
            id = id,
            name = name,
            emoji = emoji,
            unlockedAt = unlockedAt,
        )
    }

    override suspend fun failChallenge(date: String, movieId: Int) {
        val timestamp = System.currentTimeMillis()
        challengeDao.failChallenge(date, movieId, timestamp)
    }

    private fun ChallengeMovieOption.toJson(): MovieOptionJson {
        return MovieOptionJson(
            movieId = movieId,
            title = title,
            poster = poster,
            year = year,
            whyItFits = whyItFits,
            isCorrectChoice = isCorrectChoice,
        )
    }

    private fun MovieOptionJson.toDomain(): ChallengeMovieOption {
        return ChallengeMovieOption(
            movieId = movieId,
            title = title,
            poster = poster,
            year = year,
            whyItFits = whyItFits,
            isCorrectChoice = isCorrectChoice,
        )
    }

    @kotlinx.serialization.Serializable
    private data class MovieOptionJson(
        val movieId: Int,
        val title: String,
        val poster: String?,
        val year: String?,
        val whyItFits: String,
        val isCorrectChoice: Boolean = true,
    )
}

