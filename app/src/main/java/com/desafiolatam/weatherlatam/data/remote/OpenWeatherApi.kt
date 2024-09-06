package com.desafiolatam.weatherlatam.data.remote

import com.desafiolatam.weatherlatam.data.local.WeatherWrapper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenWeatherService {
    @GET("data/{id}")
    suspend fun getWeatherData(@Path("id") id: Int): Response<WeatherWrapper>
}