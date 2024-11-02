package com.entain.racetracker.domain.usecases

import com.entain.racetracker.domain.repository.RaceRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchNextToGoRacesUseCase @Inject constructor(
    private val raceRepository: RaceRepository
) {
    operator fun invoke(currentTimeInSec:Long) = flow {
        raceRepository.fetchNextToGoRaces(currentTimeInSec)
            .collect { raceState ->
                emit(value = raceState)
            }
    }
}