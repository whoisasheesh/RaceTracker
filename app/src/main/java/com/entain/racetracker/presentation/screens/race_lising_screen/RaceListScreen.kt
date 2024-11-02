package com.entain.racetracker.presentation.screens.race_lising_screen

import android.os.Build
import android.os.Process
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.entain.racetracker.data.model.RaceState
import com.entain.racetracker.data.model.RaceSummary
import com.entain.racetracker.data.network.ConnectionState
import com.entain.racetracker.presentation.components.ErrorMessage
import com.entain.racetracker.presentation.components.ExitDialog
import com.entain.racetracker.presentation.components.FilterRaceCategoryCbMenu
import com.entain.racetracker.presentation.components.LoadingIndicator
import com.entain.racetracker.presentation.components.RaceTrackerAppTopBar
import com.entain.racetracker.presentation.screens.NoInternetScreen
import com.entain.racetracker.presentation.viewmodel.RaceListViewModel
import com.entain.racetracker.ui.theme.ColorSurfaceBackground
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@RequiresApi(Build.VERSION_CODES.S)

@Composable
fun RaceListScreen() {
    val viewModel: RaceListViewModel = hiltViewModel()

    val connectionState = viewModel.connectionState.collectAsStateWithLifecycle()

    val raceState by viewModel.raceState.collectAsStateWithLifecycle()
    val nextToGoRaces by viewModel.nextToGoRaces.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val categoryCheckedState by viewModel.categoryCheckedState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorSurfaceBackground)
    ) {

        if (connectionState.value == ConnectionState.Available) {
            Column {
                RaceTrackerAppTopBar(
                    title = "Next Up"
                )
                SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
                    onRefresh = { viewModel.refreshRaces() }) {

                    Column(modifier = Modifier.fillMaxSize()) {
                        FilterRaceCategoryCbMenu(
                            checkedState = categoryCheckedState,
                            onCheckedChange = { index, checked ->
                                viewModel.filterRacesByCheckedCategory(index, checked)
                            }
                        )
                        when (raceState) {
                            is RaceState.Loading -> LoadingIndicator()
                            is RaceState.Error -> ErrorMessage((raceState as RaceState.Error).message)
                            is RaceState.Success -> {

                                RaceList(nextToGoRaces)
                            }
                        }
                    }
                }
            }
        } else {
            NoInternetScreen()
        }
    }

// showing exit dialog on back button pressed in the RaceListScreen
    val showExitDialog = remember { mutableStateOf(false) }
    if (showExitDialog.value) {
        ExitDialog {
            showExitDialog.value = false
        }
    }
    BackHandler(enabled = true) {
        if (showExitDialog.value) {
            // Exit the app
            Process.killProcess(Process.myPid())
        } else {
            showExitDialog.value = true
        }
    }
}


@Composable
fun RaceList(races: List<RaceSummary>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        itemsIndexed(races) { index, race ->
            RaceItem(race)
        }
    }
}