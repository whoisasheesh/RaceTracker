package com.entain.racetracker.domain.usecases

import com.entain.racetracker.domain.repository.RaceRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class DeleteExpiredRacesUseCase @Inject constructor(
    private val raceRepository: RaceRepository
) {
    operator fun invoke(currentTimeInSec: Long, categoryIds: List<String>) = flow {
        raceRepository.getRacesByCategoryAndDeleteExpired(currentTimeInSec, categoryIds)
            .collect { raceState ->
                emit(value = raceState)
            }
    }
}