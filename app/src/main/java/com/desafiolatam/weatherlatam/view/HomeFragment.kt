package com.desafiolatam.weatherlatam.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.desafiolatam.weatherlatam.R
import com.desafiolatam.weatherlatam.WeatherApplication
import com.desafiolatam.weatherlatam.data.CELSIUS
import com.desafiolatam.weatherlatam.data.ITEM_ID
import com.desafiolatam.weatherlatam.databinding.FragmentHomeBinding
import com.desafiolatam.weatherlatam.extension.hide
import com.desafiolatam.weatherlatam.extension.show
import com.desafiolatam.weatherlatam.model.WeatherDto
import com.desafiolatam.weatherlatam.view.adapter.WeatherAdapter
import com.desafiolatam.weatherlatam.view.viewmodel.WeatherViewModel
import com.desafiolatam.weatherlatam.view.viewmodel.WeatherViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class HomeFragment : Fragment() {

    private lateinit var adapter: WeatherAdapter
    private lateinit var tempUnit: String

    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((activity?.application as WeatherApplication).repository)
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        tempUnit = sharedPref.getString(getString(R.string.settings_temperature_unit), CELSIUS) ?: CELSIUS

        // Initialize the adapter with an empty list and the onClick function
        adapter = WeatherAdapter(
            weatherList = emptyList(),
            inCelsius = tempUnit == CELSIUS
        ) { weatherId ->
            val bundle = bundleOf(ITEM_ID to weatherId)
            findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
        }

        initRecyclerView()
        observeWeatherData()
        refresh()
        navigateToSettings()
    }

    private fun observeWeatherData() {
        lifecycleScope.launchWhenStarted {
            viewModel.weatherData.collectLatest { state ->
                when (state) {
                    is WeatherViewModel.UIState.Loading -> binding.rvWeather.show()
                    is WeatherViewModel.UIState.Success -> {
                        val list = state.data
                        if (list.isNotEmpty()) {
                            populateRecyclerView(list)
                        } else {
                            showNoDataMessage()
                        }
                    }
                    is WeatherViewModel.UIState.Error -> {
                        binding.rvWeather.hide()
                        showNoDataMessage()
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvWeather.layoutManager = LinearLayoutManager(context)
        binding.rvWeather.adapter = adapter
    }

    private fun populateRecyclerView(list: List<WeatherDto>) {
        adapter.updateWeatherList(list)
        binding.appTitle.text = getString(R.string.cities)
    }

    private fun showNoDataMessage() {
        binding.appTitle.text = getString(R.string.no_data_available)
    }

    private fun refresh() {
        binding.rvWeather.setOnClickListener {
            viewModel.refreshWeatherData()
        }
    }

    private fun navigateToSettings() {
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
