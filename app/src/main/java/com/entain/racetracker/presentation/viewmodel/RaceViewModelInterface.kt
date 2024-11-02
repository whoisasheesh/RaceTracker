package com.entain.racetracker.presentation.viewmodel

import com.entain.racetracker.data.model.RaceState
import com.entain.racetracker.data.model.RaceSummary
import com.entain.racetracker.data.network.ConnectionState
import com.entain.racetracker.data.network.NetworkConnectivityService
import kotlinx.coroutines.flow.StateFlow

interface RaceListViewModelContract {
    val connectionState: StateFlow<ConnectionState>
    val raceState: StateFlow<RaceState<List<RaceSummary>>>
    val nextToGoRaces: StateFlow<List<RaceSummary>>
    val isRefreshing: StateFlow<Boolean>
    val categoryCheckedState: StateFlow<List<Boolean>>

    fun fetchNextToGoRaces()
    fun deleteExpiredRacesAndFilterByCategory(currentTimeInSec: Long, categoryIds: List<String>)
    fun filterRacesByCheckedCategory(index: Int, checked: Boolean)
    fun refreshRaces()

    fun observeConnectivity(networkConnectivityService: NetworkConnectivityService)
}