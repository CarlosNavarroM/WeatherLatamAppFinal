package com.desafiolatam.weatherlatam

import com.desafiolatam.weatherlatam.data.WeatherRepositoryImp
import com.desafiolatam.weatherlatam.data.remote.ServiceResponse
import com.desafiolatam.weatherlatam.model.WeatherDto
import com.desafiolatam.weatherlatam.data.local.Coord
import com.desafiolatam.weatherlatam.view.viewmodel.WeatherViewModel
import com.desafiolatam.weatherlatam.view.viewmodel.WeatherViewModel.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var repository: WeatherRepositoryImp

    private lateinit var viewModel: WeatherViewModel

    // Utilizamos un TestCoroutineDispatcher para manejar corrutinas en pruebas
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = WeatherViewModel(repository)
    }

    @Test
    fun `fetchRemoteWeatherData should emit success state when repository returns data`() = runTest {
        // Simulamos la respuesta del repositorio con datos de WeatherDto
        val weatherData = listOf(
            WeatherDto(
                id = 1,
                currentTemp = 20.0,
                maxTemp = 25.0,
                minTemp = 15.0,
                pressure = 1013.0,
                humidity = 80.0,
                windSpeed = 5.0,
                sunrise = 1633065600L,
                sunset = 1633108800L,
                cityName = "Santiago",
                coord = Coord(lat = -33.45, lon = -70.65),
                weatherDescription = "Clear sky"
            )
        )
        `when`(repository.getRemoteWeatherData()).thenReturn(flowOf(ServiceResponse.Success(weatherData)))

        // Llamamos a la función
        viewModel.fetchRemoteWeatherData()

        // Avanzamos el tiempo de ejecución de las corrutinas
        dispatcher.scheduler.advanceUntilIdle()

        // Verificamos que el estado emitido sea el correcto
        assert(viewModel.weatherData.value is UIState.Success)
        assert((viewModel.weatherData.value as UIState.Success).data == weatherData)
    }

    @Test
    fun `fetchRemoteWeatherData should emit error state when repository returns error`() = runTest {
        val errorMessage = "Network error"
        `when`(repository.getRemoteWeatherData()).thenReturn(flowOf(ServiceResponse.Error(errorMessage)))

        viewModel.fetchRemoteWeatherData()

        dispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.weatherData.value is UIState.Error)
        assertEquals(errorMessage, (viewModel.weatherData.value as UIState.Error).error)
    }

    @Test
    fun `fetchWeatherDetailById should emit success state when data exists`() = runTest {
        val weatherDetail = WeatherDto(
            id = 1,
            currentTemp = 20.0,
            maxTemp = 25.0,
            minTemp = 15.0,
            pressure = 1013.0,
            humidity = 80.0,
            windSpeed = 5.0,
            sunrise = 1633065600L,
            sunset = 1633108800L,
            cityName = "Santiago",
            coord = Coord(lat = -33.45, lon = -70.65),
            weatherDescription = "Clear sky"
        )
        `when`(repository.getWeatherDataById(1)).thenReturn(flowOf(weatherDetail))

        viewModel.fetchWeatherDetailById(1)

        dispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.weatherDetail.value is UIState.Success)
        assertEquals(weatherDetail, (viewModel.weatherDetail.value as UIState.Success).data)
    }

    @Test
    fun `fetchWeatherDetailById should emit error state when data is null`() = runTest {
        `when`(repository.getWeatherDataById(1)).thenReturn(flowOf(null))

        viewModel.fetchWeatherDetailById(1)

        dispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.weatherDetail.value is UIState.Error)
        assertEquals("Data not found", (viewModel.weatherDetail.value as UIState.Error).error)
    }

    @Test
    fun `refreshWeatherData should call fetchRemoteWeatherData`() = runTest {
        // Usamos un mock para verificar que fetchRemoteWeatherData haya sido llamada
        val weatherData = listOf(
            WeatherDto(
                id = 1,
                currentTemp = 20.0,
                maxTemp = 25.0,
                minTemp = 15.0,
                pressure = 1013.0,
                humidity = 80.0,
                windSpeed = 5.0,
                sunrise = 1633065600L,
                sunset = 1633108800L,
                cityName = "Santiago",
                coord = Coord(lat = 33.45, lon = -70.65),
                weatherDescription = "Clear sky"
            )
        )
        `when`(repository.getRemoteWeatherData()).thenReturn(flowOf(ServiceResponse.Success(weatherData)))

        viewModel.refreshWeatherData()

        dispatcher.scheduler.advanceUntilIdle()

        verify(repository, times(2)).getRemoteWeatherData()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reseteamos el dispatcher principal
    }
}
