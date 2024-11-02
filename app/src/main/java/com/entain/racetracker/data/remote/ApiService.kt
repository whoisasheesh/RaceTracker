package com.entain.racetracker.data.remote

import com.entain.racetracker.data.model.RaceResponse
import com.entain.racetracker.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("rest/v1/racing/")
    suspend fun getNextToGoRaces(
        @Query("method") method: String = "nextraces",
        @Query("count") count: Int = Constants.INITIAL_RACE_SUMMARY_FETCH_COUNT
    ): RaceResponse
}