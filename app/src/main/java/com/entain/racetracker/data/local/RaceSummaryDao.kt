package com.entain.racetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRaceSummaries(raceSummaries: List<RaceSummaryEntity>)

    @Query("SELECT * FROM race_summary ORDER BY advertisedStartSeconds ASC LIMIT 5")
    fun getRaceSummaries(): Flow<List<RaceSummaryEntity>>

    @Query("SELECT * FROM race_summary WHERE categoryId IN (:categoryIds) ORDER BY advertisedStartSeconds ASC LIMIT 5")
    fun getRaceSummariesByCategory(categoryIds: List<String>): Flow<List<RaceSummaryEntity>>

    @Query("DELETE FROM race_summary")
    suspend fun clearRaceSummaries()


    @Query("DELETE FROM race_summary WHERE advertisedStartSeconds < :currentTimeInSeconds-60")
    suspend fun delExpiredRaceAfterOneMin(currentTimeInSeconds: Long)

    @Transaction
    suspend fun runInTransaction(block: suspend () -> Unit) {
        block()
    }
}