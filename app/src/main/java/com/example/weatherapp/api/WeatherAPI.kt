package com.example.weatherapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("/data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city : String,
        @Query("appid") apikey : String
    ) : Response<WeatherModel>

    @GET("/data/2.5/weather")
    suspend fun getCurrentLocationWeather(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("appid") apiKey : String
    ) : Response<WeatherModel>

}