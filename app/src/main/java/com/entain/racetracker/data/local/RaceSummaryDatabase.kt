package com.entain.racetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [RaceSummaryEntity::class], version = 1)
abstract class RaceSummaryDatabase : RoomDatabase() {
    abstract fun raceSummaryDao(): RaceSummaryDao
}