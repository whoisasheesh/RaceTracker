package com.entain.racetracker.data.model

data class RaceResponse(
    val status: String,
    val data: RaceData
)

data class RaceData(
    val next_to_go_ids: List<String>,
    val race_summaries: Map<String, RaceSummary>
)

data class RaceSummary(
    val race_id: String,
    val race_number: Int,
    val meeting_name: String,
    val category_id: String,
    val advertised_start: AdvertisedStart,
)

data class AdvertisedStart(
    val seconds: Long
)