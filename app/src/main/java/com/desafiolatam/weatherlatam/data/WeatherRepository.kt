package com.desafiolatam.weatherlatam.data

import com.desafiolatam.weatherlatam.data.remote.ServiceResponse
import com.desafiolatam.weatherlatam.model.WeatherDto
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {


    suspend fun getRemoteWeatherData(): Flow<ServiceResponse<List<WeatherDto>>>
    suspend fun getWeatherData(): Flow<List<WeatherDto>?>
    suspend fun getWeatherDataById(id: Int): Flow<WeatherDto?>
    suspend fun insertData(weatherDto: WeatherDto)
    suspend fun clearAll()
}
