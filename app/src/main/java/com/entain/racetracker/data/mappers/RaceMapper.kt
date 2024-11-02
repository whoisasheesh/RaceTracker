package com.entain.racetracker.data.mappers

import com.entain.racetracker.data.local.RaceSummaryEntity
import com.entain.racetracker.data.model.AdvertisedStart
import com.entain.racetracker.data.model.RaceSummary
import kotlin.time.Duration.Companion.seconds

fun List<RaceSummary>.mapToEntity(): List<RaceSummaryEntity> = map {
    RaceSummaryEntity(
        raceId = it.race_id,
        meetingName = it.meeting_name,
        raceNumber = it.race_number,
        categoryId = it.category_id,
        advertisedStartSeconds = it.advertised_start.seconds
    )
}

fun List<RaceSummaryEntity>.mapToDomain(): List<RaceSummary> = map {
    RaceSummary(
        race_id = it.raceId,
        meeting_name = it.meetingName,
        race_number = it.raceNumber,
        category_id = it.categoryId,
        advertised_start = AdvertisedStart(it.advertisedStartSeconds)
    )
}