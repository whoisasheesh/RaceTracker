package com.entain.racetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "race_summary")
data class RaceSummaryEntity(
    @PrimaryKey val raceId: String,
    val meetingName: String,
    val raceNumber: Int,
    val categoryId: String,
    val advertisedStartSeconds: Long
)
