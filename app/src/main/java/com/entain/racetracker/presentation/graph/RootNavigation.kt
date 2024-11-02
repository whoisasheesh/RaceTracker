package com.entain.racetracker.presentation.graph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.entain.racetracker.presentation.screens.race_lising_screen.RaceListScreen
import com.entain.racetracker.presentation.screens.SplashScreen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RootNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = RaceTrackerAppScreen.SPLASH.route
    ) {
        composable(route = RaceTrackerAppScreen.SPLASH.route) {
            SplashScreen(navController = navController)
        }

        composable(route = RaceTrackerAppScreen.RACE_LIST.route) {
            RaceListScreen()
        }
    }

}

object Graph {
    const val ROOT = "root_graph"
}

sealed class RaceTrackerAppScreen(val route: String) {
    object SPLASH : RaceTrackerAppScreen(route = "splash")
    object RACE_LIST : RaceTrackerAppScreen(route = "race_list_screen")
}