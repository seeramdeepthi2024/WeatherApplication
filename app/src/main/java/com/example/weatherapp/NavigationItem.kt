package com.example.weatherapp

import com.example.weatherApp.R

sealed class NavigationItem(val route: String, val icon: Int, val title: String) {
    object CurrentWeather :
        NavigationItem(Routes.weather, R.drawable.current_weather, "Current Weather")

    object Forecasting :
        NavigationItem(Routes.forecasting, R.drawable.weather_forecasting, "Forecasting")

}