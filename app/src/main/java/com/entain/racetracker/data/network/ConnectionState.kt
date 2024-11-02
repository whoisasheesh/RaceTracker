package com.entain.racetracker.data.network

sealed class ConnectionState {
    data object Available : ConnectionState()
    data object Lost : ConnectionState()
}