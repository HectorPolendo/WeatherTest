package com.hectorpolendo.weathertest.data.datasource.service

import com.hectorpolendo.weathertest.data.model.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherDto
}