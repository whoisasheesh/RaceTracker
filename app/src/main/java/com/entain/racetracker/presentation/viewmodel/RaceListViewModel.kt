package com.entain.racetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entain.racetracker.data.enum.RaceCategory
import com.entain.racetracker.data.model.RaceState
import com.entain.racetracker.data.model.RaceSummary
import com.entain.racetracker.data.network.ConnectionState
import com.entain.racetracker.data.network.NetworkConnectivityService
import com.entain.racetracker.domain.usecases.DeleteExpiredRacesUseCase
import com.entain.racetracker.domain.usecases.FetchNextToGoRacesUseCase
import com.entain.racetracker.utils.Constants.Companion.THOUSAND_MILLISECONDS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RaceListViewModel @Inject constructor(
    private val fetchNextToGoRacesUseCase: FetchNextToGoRacesUseCase,
    private val deleteExpiredRacesUseCase: DeleteExpiredRacesUseCase,
    networkConnectivityService: NetworkConnectivityService
) :
    ViewModel(), RaceListViewModelContract {


    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Lost)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _raceState = MutableStateFlow<RaceState<List<RaceSummary>>>(RaceState.Loading())
    override val raceState: StateFlow<RaceState<List<RaceSummary>>> = _raceState.asStateFlow()

    private val _nextToGoRaces = MutableStateFlow<MutableList<RaceSummary>>(mutableListOf())
    override val nextToGoRaces: StateFlow<List<RaceSummary>> = _nextToGoRaces.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    override val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _categoryCheckedState = MutableStateFlow(List(RaceCategory.entries.size) { false })
    override val categoryCheckedState: StateFlow<List<Boolean>> =
        _categoryCheckedState.asStateFlow()


    init {
        observeConnectivity(networkConnectivityService)
        fetchNextToGoRaces()
        startTicker()
    }

    override fun observeConnectivity(networkConnectivityService: NetworkConnectivityService) {
        viewModelScope.launch {
            networkConnectivityService.connectionState.collect { connectionState ->
                _connectionState.value = connectionState
            }
        }
    }

    private fun startTicker() {
        viewModelScope.launch {
            tickerFlow(THOUSAND_MILLISECONDS).collect {
                deleteExpiredRacesAndFilterByCategory(
                    System.currentTimeMillis() / 1000,
                    getSelectedCategories()
                )
            }
        }
    }

    private fun tickerFlow(period: Long): Flow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis() / 1000)
            delay(period)
        }
    }

    override fun fetchNextToGoRaces() {
        viewModelScope.launch {
            fetchNextToGoRacesUseCase.invoke(System.currentTimeMillis() / 1000)
                .collectLatest { state ->
                    _raceState.value = state

                    when (state) {
                        is RaceState.Success -> {
                            state.data?.let { raceSummaries ->
                                _nextToGoRaces.value.clear()
                                _nextToGoRaces.value.addAll(raceSummaries)
                            }
                        }

                        is RaceState.Error -> {
                            _nextToGoRaces.value.clear()
                        }

                        is RaceState.Loading -> {}
                    }
                }
        }
    }

    override fun deleteExpiredRacesAndFilterByCategory(
        currentTimeInSec: Long,
        categoryIds: List<String>
    ) {
        viewModelScope.launch {
            deleteExpiredRacesUseCase.invoke(currentTimeInSec, categoryIds).collectLatest { state ->
                when (state) {
                    is RaceState.Success -> {
                        state.data?.let { raceSummaries ->
                            val tempIndex: Int = _nextToGoRaces.value.size
                            if (tempIndex > 0 && raceSummaries.isEmpty()) {
                                _isRefreshing.value = true
//                                fetchNextToGoRaces() // do this if _isRefreshing.value = true does not work
                            }
                            _nextToGoRaces.value = raceSummaries.toMutableList()
                            _raceState.value = state
                        }
                    }

                    is RaceState.Error -> {
                        _nextToGoRaces.value.clear()
                        _raceState.value = state
                    }

                    is RaceState.Loading -> {}
                }

            }
        }
    }

    override fun filterRacesByCheckedCategory(index: Int, checked: Boolean) {
        _categoryCheckedState.value = _categoryCheckedState.value.toMutableList().apply {
            this[index] = checked
        }
        if (getSelectedCategories().isNotEmpty()) {
            deleteExpiredRacesAndFilterByCategory(
                System.currentTimeMillis() / 1000,
                getSelectedCategories()
            )
        } else {
            fetchNextToGoRaces()
        }
    }

    override fun refreshRaces() {
        viewModelScope.launch {
            _isRefreshing.value = true

            try {
                if (getSelectedCategories().isNotEmpty()) {
                    deleteExpiredRacesAndFilterByCategory(
                        System.currentTimeMillis() / 1000,
                        getSelectedCategories()
                    )
                } else {
                    fetchNextToGoRaces()
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun getSelectedCategories(): List<String> {
        return RaceCategory.entries
            .filterIndexed { i, _ -> categoryCheckedState.value[i] }
            .map { it.categoryId }
            .takeIf { it.isNotEmpty() } ?: emptyList()
    }

}