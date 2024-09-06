package com.desafiolatam.weatherlatam

import com.desafiolatam.weatherlatam.data.WeatherRepositoryImp
import com.desafiolatam.weatherlatam.data.local.Clouds
import com.desafiolatam.weatherlatam.data.local.Coord
import com.desafiolatam.weatherlatam.data.local.Main
import com.desafiolatam.weatherlatam.data.local.Rain
import com.desafiolatam.weatherlatam.data.local.Sys
import com.desafiolatam.weatherlatam.data.local.WeatherWrapper
import com.desafiolatam.weatherlatam.data.local.WeatherDao
import com.desafiolatam.weatherlatam.data.local.Wind
import com.desafiolatam.weatherlatam.data.remote.OpenWeatherService
import com.desafiolatam.weatherlatam.data.remote.ServiceResponse
import com.desafiolatam.weatherlatam.data.toEntity
import com.desafiolatam.weatherlatam.model.WeatherDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImpTest {

    @Mock
    private lateinit var weatherDao: WeatherDao

    @Mock
    private lateinit var openWeatherService: OpenWeatherService

    private lateinit var weatherRepository: WeatherRepositoryImp

    // Para testear con un Dispatcher de prueba
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        weatherRepository = WeatherRepositoryImp(weatherDao)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `getRemoteWeatherData returns success when data is available`() = runTest {
        // Datos simulados
        val mockResponse = mock(Response::class.java) as Response<WeatherWrapper>
        val mockWeatherWrapper = WeatherWrapper(
            coord = Coord(0.0, 0.0),
            weather = listOf(),
            base = "stations",
            main = Main(20.0, 18.0, 25.0, 1010, 60),
            visibility = 10000,
            wind = Wind(5.0, 180, 7.0),
            rain = Rain(0.0),
            clouds = Clouds(75),
            dt = 1627849200,
            sys = Sys(1, 123456, "CL", 1627824000, 1627870800),
            timezone = -10800,
            id = 3871336,
            name = "Santiago",
            cod = 200
        )

        // Configuramos el mock del servicio para devolver una respuesta exitosa
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(mockWeatherWrapper)
        `when`(openWeatherService.getWeatherData(anyInt())).thenReturn(mockResponse)

        // Ejecutamos el método a testear
        val result = weatherRepository.getRemoteWeatherData().first()

        // Verificamos que el resultado sea exitoso
        assertTrue(result is ServiceResponse.Success)
        assertEquals(1, (result as ServiceResponse.Success).data.size)
        assertEquals("Santiago", result.data[0].cityName)
    }

    @Test
    fun `getRemoteWeatherData returns error when response is unsuccessful`() = runTest {
        // Datos simulados para una respuesta fallida
        val mockResponse = mock(Response::class.java) as Response<WeatherWrapper>

        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.code()).thenReturn(404)
        `when`(openWeatherService.getWeatherData(anyInt())).thenReturn(mockResponse)

        // Ejecutamos el método a testear
        val result = weatherRepository.getRemoteWeatherData().first()

        // Verificamos que el resultado sea un error
        assertTrue(result is ServiceResponse.Error)
        assertEquals("Not Found", (result as ServiceResponse.Error).error)
    }

    @Test
    fun `getWeatherDataById returns the correct weather data`() = runTest {
        // Datos simulados
        val mockWeatherDto = WeatherDto(0, 20.0, 25.0, 15.0, 1010.0, 60.0, 5.0, 1627849200, 1627892400, "Santiago", Coord(0.0, 0.0), "Sunny")

        // Configuramos el mock del Dao para devolver los datos simulados
        `when`(weatherDao.getWeatherDataById(0)).thenReturn(flowOf(mockWeatherDto.toEntity()))

        // Ejecutamos el método a testear
        val result = weatherRepository.getWeatherDataById(0).first()

        // Verificamos que el resultado sea el correcto
        assertEquals("Santiago", result?.cityName)
    }

    @Test
    fun `insertData calls the dao's insertData`() = runTest {
        // Datos simulados
        val mockWeatherDto = WeatherDto(0, 20.0, 25.0, 15.0, 1010.0, 60.0, 5.0, 1627849200, 1627892400, "Santiago", Coord(0.0, 0.0), "Sunny")

        // Ejecutamos el método a testear
        weatherRepository.insertData(mockWeatherDto)

        // Verificamos que el Dao haya sido llamado
        verify(weatherDao).insertData(mockWeatherDto.toEntity())
    }

    @Test
    fun `clearAll calls the dao's clearAll method`() = runTest {
        // Ejecutamos el método a testear
        weatherRepository.clearAll()

        // Verificamos que el Dao haya sido llamado
        verify(weatherDao).clearAll()
    }
}
