package com.desafiolatam.weatherlatam.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desafiolatam.weatherlatam.R
import com.desafiolatam.weatherlatam.databinding.ItemWeatherBinding
import com.desafiolatam.weatherlatam.extension.toFahrenheit
import com.desafiolatam.weatherlatam.extension.toCelsius
import com.desafiolatam.weatherlatam.model.WeatherDto

class WeatherAdapter(
    private var weatherList: List<WeatherDto>,
    private val inCelsius: Boolean, // Indica si se debe mostrar en Celsius o Fahrenheit
    private val onClick: (Int) -> Unit // onClick es obligatorio para manejar clics en la lista
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val itemBinding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) =
        holder.onBind(weatherList[position])

    override fun getItemCount(): Int = weatherList.size

    fun updateWeatherList(newList: List<WeatherDto>) {
        weatherList = newList
        notifyDataSetChanged() // Notificar después de actualizar la lista completa
    }

    inner class WeatherViewHolder(private val itemBinding: ItemWeatherBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun onBind(weatherDto: WeatherDto) {
            // Formateo de la temperatura según la unidad seleccionada
            Log.d("WeatherAdapter", "Binding city with ID: ${weatherDto.id}")
            val currentTemp = if (inCelsius) {
                String.format("%.1f°C", weatherDto.currentTemp.toCelsius()) // Convierte Kelvin a Celsius
            } else {
                String.format("%.1f°F", weatherDto.currentTemp.toFahrenheit()) // Convierte Kelvin a Fahrenheit
            }

            // Normalizar la descripción del clima para asegurar coincidencias
            val normalizedDescription = weatherDto.weatherDescription.lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }

            // Asignar el ícono según la descripción del clima
            val weatherIconRes = when (normalizedDescription) {
                "Light rain" -> R.drawable.light_rain
                "Snowy" -> R.drawable.cloudy_snowing
                "Clear sky" -> R.drawable.sun
                "Overcast clouds" -> R.drawable.cloudy
                else -> R.drawable.cloud // Opcional: un ícono por defecto
            }

            // Asignar el texto formateado e ícono a las vistas correspondientes
            itemBinding.temperature.text = currentTemp
            itemBinding.cityName.text = weatherDto.cityName // Nombre de la ciudad
            itemBinding.weatherDescription.text = normalizedDescription // Descripción del clima
            itemBinding.weatherIcon.setImageResource(weatherIconRes) // Asigna el ícono del clima

            // Agregar evento de clic para abrir los detalles de la ciudad
            itemBinding.root.setOnClickListener {
                onClick(weatherDto.id) // Ejecutar la función de clic con el ID de la ciudad
            }
        }
    }
}
