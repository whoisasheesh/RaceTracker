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
            // Emiting a loading state when the data is being fetched
            emit(RaceState.Loading())
            try {
                // fetching the next to go races from the API
                val raceResponseData = apiService.getNextToGoRaces()

                // filtering the race summaries to include the race that is below one minute past the advertised time along with other races
                val raceSummaries = raceResponseData.data.race_summaries.values
                    .filter { it.advertised_start.seconds >= currentTimeInSec - 60 }
                    .toList()
                    .mapToEntity() // mapping the race summaries to database entity

                // once done clearing the race summary dao and adding the next to go races in the race summary dao
                raceSummaryDao.runInTransaction {
                    raceSummaryDao.clearRaceSummaries()
                    raceSummaryDao.insertRaceSummaries(raceSummaries)
                }

                val raceSummariesFromDb =
                    raceSummaryDao.getRaceSummaries().first().mapToDomain()

                //mapping the race summaries from race summary dao to Domain and then emitted in the success state as below
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

            // Fetching race summaries based on the filtered race category IDs
            val raceSummaries = if (categoryIds.isEmpty()) {
                raceSummaryDao.getRaceSummaries().first()
            } else {
                raceSummaryDao.getRaceSummariesByCategory(categoryIds).first()
            }
            if (raceSummaries.isEmpty()) {
                emit(RaceState.Error("No upcoming races found!"))
            } else {
                val raceSummaryList = raceSummaries.mapToDomain()
                emit(RaceState.Success(raceSummaryList))
            }
        } catch (e: Exception) {
            emit(RaceState.Error(e.message ?: "Unknown error occurred!"))
        }
    }.flowOn(Dispatchers.IO)
}

