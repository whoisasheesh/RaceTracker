package com.entain.racetracker.data.model

sealed class RaceState<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : RaceState<T>(data)
    class Error<T>(message: String, data: T? = null) : RaceState<T>(data, message)
    class Loading<T> : RaceState<T>()
}
