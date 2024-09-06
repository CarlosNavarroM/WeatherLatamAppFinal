package com.desafiolatam.weatherlatam.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desafiolatam.weatherlatam.data.WeatherRepositoryImp
import com.desafiolatam.weatherlatam.data.remote.ServiceResponse
import com.desafiolatam.weatherlatam.model.WeatherDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepositoryImp) : ViewModel() {

    // StateFlow para la lista principal de datos del clima
    private val _weatherData = MutableStateFlow<UIState<List<WeatherDto>>>(UIState.Loading(true))
    val weatherData: StateFlow<UIState<List<WeatherDto>>> get() = _weatherData

    // StateFlow para los detalles de una ubicación específica
    private val _weatherDetail = MutableStateFlow<UIState<WeatherDto?>>(UIState.Loading(true))
    val weatherDetail: StateFlow<UIState<WeatherDto?>> get() = _weatherDetail

    init {
        // Cargar datos remotos al iniciar
        fetchRemoteWeatherData()
    }

    // Función para obtener los datos de la API
    fun fetchRemoteWeatherData() {
        viewModelScope.launch {
            val response = repository.getRemoteWeatherData().first()  // Cambia a first
            when (response) {
                is ServiceResponse.Loading -> _weatherData.value = UIState.Loading(true)
                is ServiceResponse.Success -> _weatherData.value = UIState.Success(response.data)
                is ServiceResponse.Error -> _weatherData.value = UIState.Error(response.error)
            }
        }
    }
    // Función para refrescar los datos
    fun refreshWeatherData() {
        fetchRemoteWeatherData()
    }

    // Función para obtener los detalles de un elemento específico por ID desde la API
    fun fetchWeatherDetailById(id: Int) {
        viewModelScope.launch {
            _weatherDetail.value = UIState.Loading(true)
            repository.getWeatherDataById(id).collect { weatherData ->
                if (weatherData != null) {
                    _weatherDetail.value = UIState.Success(weatherData)
                } else {
                    _weatherDetail.value = UIState.Error("Data not found")
                }
            }
        }
    }

    // Clases selladas para manejar los diferentes estados de UI (cargando, éxito, error)
    sealed class UIState<T> {
        class Loading<T>(val isLoading: Boolean) : UIState<T>()
        class Success<T>(val data: T) : UIState<T>()
        class Error<T>(val error: String) : UIState<T>()
    }
}
