package com.desafiolatam.weatherlatam.data

import com.desafiolatam.weatherlatam.data.local.WeatherDao
import com.desafiolatam.weatherlatam.data.remote.OpenWeatherService
import com.desafiolatam.weatherlatam.data.remote.RetrofitClient
import com.desafiolatam.weatherlatam.data.remote.ServiceResponse
import com.desafiolatam.weatherlatam.model.WeatherDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WeatherRepositoryImp(
    private val weatherDao: WeatherDao
) : WeatherRepository {

    override suspend fun getRemoteWeatherData(): Flow<ServiceResponse<List<WeatherDto>>> {
        val data: MutableStateFlow<ServiceResponse<List<WeatherDto>>> =
            MutableStateFlow(ServiceResponse.Loading(true))

        val service = RetrofitClient.getInstance().create(OpenWeatherService::class.java)

        val weatherList = mutableListOf<WeatherDto>()

        val responses = listOf(
            withContext(Dispatchers.IO) { service.getWeatherData(id = 0) },
            withContext(Dispatchers.IO) { service.getWeatherData(id = 1) },
            withContext(Dispatchers.IO) { service.getWeatherData(id = 2) }
        )

        responses.forEachIndexed { index, response ->
            when {
                response.isSuccessful -> {
                    response.body()?.toWeatherDto()?.let { weatherDto ->
                        // Asegúrate de que cada weatherDto tenga un ID único
                        weatherDto.id = index
                        weatherList.add(weatherDto)
                        withContext(Dispatchers.IO) {
                            insertData(weatherDto)
                        }
                    }
                }
                else -> {
                    data.value = when (response.code()) {
                        401 -> ServiceResponse.Error(Unauthorized)
                        404 -> ServiceResponse.Error(NotFound)
                        500 -> ServiceResponse.Error(InternalServerError)
                        503 -> ServiceResponse.Error(ServiceUnavailable)
                        else -> ServiceResponse.Error("Error desconocido")
                    }
                    return@forEachIndexed
                }
            }
        }

        data.value = if (weatherList.isNotEmpty()) {
            ServiceResponse.Success(weatherList)
        } else {
            ServiceResponse.Error("No se pudieron cargar los datos de las ciudades")
        }

        return flowOf(data.value)
    }

    override suspend fun getWeatherData(): Flow<List<WeatherDto>?> =
        withContext(Dispatchers.IO) {
            weatherDao.getWeatherData().map { entityList ->
                entityList?.let { entityListToDtoList(it) }
            }
        }

    override suspend fun getWeatherDataById(id: Int): Flow<WeatherDto?> =
        withContext(Dispatchers.IO) {
            weatherDao.getWeatherDataById(id).map { entity ->
                entity?.let { entityToDto(it) }
            }
        }

    override suspend fun insertData(weatherDto: WeatherDto) =
        withContext(Dispatchers.IO) {
            weatherDao.insertData(weatherDto.toEntity())
        }

    override suspend fun clearAll() =
        withContext(Dispatchers.IO) {
            weatherDao.clearAll()
        }



}
