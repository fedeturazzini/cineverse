package com.ft.architectcoders.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        DbMovie::class,
        DbProfile::class,
        DbDuelSession::class,
        DbDuelChoice::class,
        DbTasteFingerprint::class,
        DbMarathon::class,
        DbMarathonPick::class,
        DbDailyChallenge::class,
        DbChallengeBadge::class,
        DbAiSearchSession::class,
    ],
    version = 5,
    exportSchema = false,
)
abstract class CineVerseDatabase : RoomDatabase() {
    abstract val moviesDao: MoviesDao
    abstract val profileDao: ProfileDao
    abstract val duelDao: DuelDao
    abstract val marathonDao: MarathonDao
    abstract val challengeDao: ChallengeDao
    abstract val aiSearchDao: AiSearchDao
}
