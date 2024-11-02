package com.entain.racetracker.data.repository

import com.entain.racetracker.data.local.RaceSummaryDao
import com.entain.racetracker.data.mappers.mapToDomain
import com.entain.racetracker.data.mappers.mapToEntity
import com.entain.racetracker.data.model.RaceState
import com.entain.racetracker.data.model.RaceSummary
import com.entain.racetracker.data.remote.ApiService
import com.entain.racetracker.domain.repository.RaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RaceRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val raceSummaryDao: RaceSummaryDao
) : RaceRepository {
    override fun fetchNextToGoRaces(currentTimeInSec: Long): Flow<RaceState<List<RaceSummary>>> =
        flow {
            emit(RaceState.Loading())
            try {
                val raceResponseData = apiService.getNextToGoRaces()
                val raceSummaries = raceResponseData.data.race_summaries.values
                    .filter { it.advertised_start.seconds >= currentTimeInSec - 60 }
                    .toList()
                    .mapToEntity()

                raceSummaryDao.runInTransaction {
                    raceSummaryDao.clearRaceSummaries()
                    raceSummaryDao.insertRaceSummaries(raceSummaries)
                }

                val raceSummariesFromDb =
                    raceSummaryDao.getRaceSummaries().first().mapToDomain()
                emit(RaceState.Success(raceSummariesFromDb))

            } catch (e: Exception) {
                emit(
                    RaceState.Error(
                        e.message ?: "Unknown error occurred!"
                    )
                )
            }
        }.flowOn(Dispatchers.IO)

    override fun getRacesByCategoryAndDeleteExpired(
        currentTimeInSec: Long, categoryIds: List<String>
    ): Flow<RaceState<List<RaceSummary>>> = flow {
        try {
            raceSummaryDao.delExpiredRaceAfterOneMin(currentTimeInSec)
            val raceSummaries = if (categoryIds.isEmpty()) {
                raceSummaryDao.getRaceSummaries().first()
            } else {
                raceSummaryDao.getRaceSummariesByCategory(categoryIds).first()
            }
            if (raceSummaries.isEmpty()) {
                emit(RaceState.Error("No race summaries found for selected category!"))
            } else {
                val raceSummaryList = raceSummaries.mapToDomain()
                emit(RaceState.Success(raceSummaryList))
            }
        } catch (e: Exception) {
            emit(RaceState.Error(e.message ?: "Unknown error occurred!"))
        }
    }.flowOn(Dispatchers.IO)
}

