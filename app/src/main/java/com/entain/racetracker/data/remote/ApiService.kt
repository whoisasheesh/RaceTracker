package com.entain.racetracker.data.remote

import com.entain.racetracker.data.model.RaceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("rest/v1/racing/")
    suspend fun getNextToGoRaces(
        @Query("method") method: String = "nextraces",
        @Query("count") count: Int = 2
    ): RaceResponse
}