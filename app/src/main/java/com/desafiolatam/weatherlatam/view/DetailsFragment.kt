package com.desafiolatam.weatherlatam.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.desafiolatam.weatherlatam.R
import com.desafiolatam.weatherlatam.WeatherApplication
import com.desafiolatam.weatherlatam.data.CELSIUS
import com.desafiolatam.weatherlatam.data.ITEM_ID
import com.desafiolatam.weatherlatam.databinding.FragmentDetailsBinding
import com.desafiolatam.weatherlatam.extension.toFahrenheit
import com.desafiolatam.weatherlatam.extension.toShortDateString
import com.desafiolatam.weatherlatam.model.WeatherDto
import com.desafiolatam.weatherlatam.view.viewmodel.WeatherViewModel
import com.desafiolatam.weatherlatam.view.viewmodel.WeatherViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((activity?.application as WeatherApplication).repository)
    }

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private var weatherInfoId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        weatherInfoId = arguments?.getInt(ITEM_ID) ?: -1
        Log.d("DetailsFragment", "Received city ID: $weatherInfoId")

       if (weatherInfoId != -1) {
            fetchWeatherDetail(weatherInfoId)
        } else {
            Toast.makeText(context, "ID no encontrado", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun fetchWeatherDetail(id: Int) {
        Log.d("DetailsFragment", "Fetching weather detail for ID: $id")
        viewModel.fetchWeatherDetailById(id)
        observeWeatherDetail()
    }

    private fun observeWeatherDetail() {
        lifecycleScope.launchWhenStarted {
            viewModel.weatherDetail.collectLatest { state ->
                when (state) {
                    is WeatherViewModel.UIState.Loading -> {
                        Log.d("DetailsFragment", "Loading weather detail")
                    }
                    is WeatherViewModel.UIState.Success -> {
                        val weather = state.data
                        Log.d("DetailsFragment", "Weather detail fetched: $weather")
                        updateUI(weather)
                    }
                    is WeatherViewModel.UIState.Error -> {
                        Log.e("DetailsFragment", "Error fetching weather detail: ${state.error}")
                        binding.cityName.text = getString(R.string.no_data_available)
                    }
                }
            }
        }
    }

    private fun updateUI(weather: WeatherDto?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val tempUnit = sharedPref.getString(getString(R.string.settings_temperature_unit), CELSIUS)
        val windSpeedUnit = sharedPref.getString(getString(R.string.settings_wind_speed_unit), "m/s")

        weather?.let {
            // Redondear la temperatura correctamente y mostrar con °C o °F
            val currentTemp = if (tempUnit == CELSIUS) {
                String.format("%.1f°C", it.currentTemp - 273.15) // Convertir de Kelvin a Celsius
            } else {
                String.format("%.1f°F", it.currentTemp.toFahrenheit()) // Convertir de Kelvin a Fahrenheit
            }

            val maxTemp = if (tempUnit == CELSIUS) {
                String.format("%.1f°C", it.maxTemp - 273.15)
            } else {
                String.format("%.1f°F", it.maxTemp.toFahrenheit())
            }

            val minTemp = if (tempUnit == CELSIUS) {
                String.format("%.1f°C", it.minTemp - 273.15)
            } else {
                String.format("%.1f°F", it.minTemp.toFahrenheit())
            }

            // Conversión de la velocidad del viento dependiendo de la unidad seleccionada
            val windSpeed = if (windSpeedUnit == "m/s") {
                String.format("%.1f m/s", it.windSpeed)
            } else {
                String.format("%.1f mph", it.windSpeed * 2.23694) // Conversión de m/s a mph
            }

            // Actualizar UI con los valores formateados
            binding.currentTemp.text = currentTemp
            binding.maxTemp.text = getString(R.string.max_temp, maxTemp)
            binding.minTemp.text = getString(R.string.min_temp, minTemp)
            binding.pressure.text = getString(R.string.pressure, it.pressure.toString())
            binding.humidity.text = getString(R.string.humidity, it.humidity.toString())
            binding.windSpeed.text = getString(R.string.wind_speed, windSpeed)
            binding.sunrise.text = getString(R.string.sunrise, it.sunrise.toShortDateString())
            binding.sunset.text = getString(R.string.sunset, it.sunset.toShortDateString())
            binding.cityName.text = it.cityName

            // Intent para abrir Google Maps con las coordenadas de la ciudad
            binding.mapIcon.setOnClickListener {
                val gmmIntentUri =
                    Uri.parse("geo:${weather.coord.lat},${weather.coord.lon}?q=${weather.cityName}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ITEM_ID, weatherInfoId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
