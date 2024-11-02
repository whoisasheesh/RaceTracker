package com.entain.racetracker.domain.repository

import com.entain.racetracker.data.model.RaceSummary
import com.entain.racetracker.data.model.RaceState
import kotlinx.coroutines.flow.Flow

interface RaceRepository {
    fun fetchNextToGoRaces(currentTimeInSec: Long): Flow<RaceState<List<RaceSummary>>>
    fun getRacesByCategoryAndDeleteExpired(
        currentTimeInSec: Long, categoryIds: List<String>
    ): Flow<RaceState<List<RaceSummary>>>
}