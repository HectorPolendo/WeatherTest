package com.hectorpolendo.weathertest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hectorpolendo.weathertest.domain.usecase.GetWeatherUseCase

class WeatherViewModelFactory(
    private val useCase: GetWeatherUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(useCase) as T
    }
}