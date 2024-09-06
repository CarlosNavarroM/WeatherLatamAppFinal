package com.desafiolatam.weatherlatam.model

import com.desafiolatam.weatherlatam.data.local.Coord

data class WeatherDto(
    var id: Int,
    val currentTemp: Double,
    val maxTemp: Double,
    val minTemp: Double,
    val pressure: Double,
    val humidity: Double,
    val windSpeed: Double,
    val sunrise: Long,
    val sunset: Long,
    val cityName: String,
    val coord: Coord,
    val weatherDescription: String // Add this field
)

data class Coord(
    val lat: Double,
    val lon: Double
)
