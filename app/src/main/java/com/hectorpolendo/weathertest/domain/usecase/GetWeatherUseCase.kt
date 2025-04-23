package com.hectorpolendo.weathertest.domain.usecase

import com.hectorpolendo.weathertest.domain.repository.WeatherRepository

class GetWeatherUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String) = repository.getWeather(city)
}