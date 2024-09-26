package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.WeatherModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository:WeatherRepository,
    private val sharedPreferences: SharedPreferences): ViewModel() {

    val current: Flow<WeatherModel> = repository.getCurrentWeather()
    private val _selectedCityWeather = MutableStateFlow<WeatherModel?>(null)
    val selectedCityWeather : StateFlow<WeatherModel?> get() = _selectedCityWeather

    private val _selectedCity = MutableStateFlow("")
    val selectedCity: StateFlow<String> get() = _selectedCity

    private val _weatherData = MutableStateFlow<Result<WeatherModel>?>(null)
    val weatherData: StateFlow<Result<WeatherModel>?> get() = _weatherData

    init {
        // Load previous city from SharedPreferences
        loadPreviousCity()
    }

    private fun loadPreviousCity() {
        val city = sharedPreferences.getString("selected_city", "")
        if (!city.isNullOrEmpty()) {
            _selectedCity.value = city
            fetchSelectedCityWeather(city) // Fetch weather for the previous city
        }
    }

    fun updateSelectedCity(city: String) {
        _selectedCity.value = city // Update the selected city
        fetchSelectedCityWeather(city) // Fetch the weather for the newly selected city
        saveSelectedCity(city) // Save the selected city in SharedPreferences
    }

    private fun saveSelectedCity(city: String) {
        sharedPreferences.edit().putString("selected_city", city).apply()
    }

    fun fetchSelectedCityWeather(city: String) {
        viewModelScope.launch {
            repository.getSelectedCityWeather(city).collect { result ->
                _selectedCityWeather.value = result // Collect and update weather data
            }
        }
    }


}