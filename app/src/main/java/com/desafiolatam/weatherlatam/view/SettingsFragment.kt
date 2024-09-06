package com.desafiolatam.weatherlatam.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.desafiolatam.weatherlatam.R
import com.desafiolatam.weatherlatam.data.CELSIUS
import com.desafiolatam.weatherlatam.data.FAHRENHEIT
import com.desafiolatam.weatherlatam.data.METERS_PER_SECOND
import com.desafiolatam.weatherlatam.data.MILES_PER_HOUR
import com.desafiolatam.weatherlatam.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        // Cargar la preferencia guardada de temperatura (Celsius o Fahrenheit)
        sharedPref.getString(getString(R.string.settings_temperature_unit), CELSIUS)?.let { unit ->
            binding.rbMetric.isChecked = unit == CELSIUS
            binding.rbImperial.isChecked = unit == FAHRENHEIT
        }

        // Cargar la preferencia guardada de velocidad del viento (m/s o mph)
        sharedPref.getString(getString(R.string.settings_wind_speed_unit), METERS_PER_SECOND)?.let { unit ->
            binding.rbMetersPerSecond.isChecked = unit == METERS_PER_SECOND
            binding.rbMilesPerHour.isChecked = unit == MILES_PER_HOUR
        }

        // Guardar la preferencia seleccionada de temperatura
        binding.rbMetric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) saveTemperaturePreference(CELSIUS)
        }

        binding.rbImperial.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) saveTemperaturePreference(FAHRENHEIT)
        }

        // Guardar la preferencia seleccionada de velocidad del viento
        binding.rbMetersPerSecond.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) saveWindSpeedPreference(METERS_PER_SECOND)
        }

        binding.rbMilesPerHour.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) saveWindSpeedPreference(MILES_PER_HOUR)
        }
    }

    private fun saveTemperaturePreference(unit: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.settings_temperature_unit), unit)
            apply()
        }
    }

    private fun saveWindSpeedPreference(unit: String) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.settings_wind_speed_unit), unit)
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
