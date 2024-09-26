package com.example.weatherapp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.example.weatherapp.api.Constant
import com.example.weatherapp.api.WeatherAPI
import com.example.weatherapp.api.WeatherModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val service: WeatherAPI,
    private val application: Application
){


@RequiresPermission(ACCESS_FINE_LOCATION)
fun getCurrentWeather(): Flow<WeatherModel> {
    return locationFlow().map {
        service.getCurrentLocationWeather(it.latitude, it.longitude, Constant.apiKey)
            .body()
    }.filterNotNull()
}

    @RequiresPermission(ACCESS_FINE_LOCATION)
    private fun locationFlow() = channelFlow<Location> {
        val client = LocationServices.getFusedLocationProviderClient(application)
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }
        val request = LocationRequest.create()
            .setInterval(10_000)
            .setFastestInterval(5_000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(170f)

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun getSelectedCityWeather(city: String): Flow<WeatherModel> {
        return locationFlow().map {
            service.getWeather(city, Constant.apiKey)
                .body()
        }.filterNotNull()
    }


}
