package com.ft.architectcoders.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MarathonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarathon(marathon: DbMarathon): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicks(picks: List<DbMarathonPick>)

    @Query("SELECT * FROM marathons WHERE id = :marathonId")
    suspend fun getMarathonById(marathonId: Long): DbMarathon?

    @Query("SELECT * FROM marathon_picks WHERE marathonId = :marathonId ORDER BY `order`")
    suspend fun getPicksByMarathonId(marathonId: Long): List<DbMarathonPick>

    @Query("SELECT * FROM marathons ORDER BY timestamp DESC")
    fun getAllMarathons(): Flow<List<DbMarathon>>

    @Query("SELECT COUNT(*) FROM marathon_picks WHERE marathonId = :marathonId")
    suspend fun getPicksCount(marathonId: Long): Int

    @Query("DELETE FROM marathons WHERE id = :marathonId")
    suspend fun deleteMarathon(marathonId: Long)

    @Transaction
    suspend fun saveMarathonWithPicks(
        marathon: DbMarathon,
        picks: List<DbMarathonPick>,
    ): Long {
        val marathonId = insertMarathon(marathon)
        val picksWithMarathonId = picks.map { it.copy(marathonId = marathonId) }
        insertPicks(picksWithMarathonId)
        return marathonId
    }
}

