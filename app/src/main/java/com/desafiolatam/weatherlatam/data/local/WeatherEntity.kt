package com.desafiolatam.weatherlatam.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val currentTemp: Double,
    val maxTemp: Double,
    val minTemp: Double,
    val pressure: Double,
    val humidity: Double,
    val windSpeed: Double,
    val sunrise: Long,
    val sunset: Long,
    val cityName: String,
    val coordLat: Double,
    val coordLon: Double,
    val weatherDescription: String // Añadir la descripción aquí

)
