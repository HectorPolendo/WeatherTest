package com.hectorpolendo.weathertest.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hectorpolendo.weathertest.domain.model.WeatherEntity
import com.hectorpolendo.weathertest.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val useCase: GetWeatherUseCase
) : ViewModel() {
    private val _weather = MutableLiveData<WeatherEntity>()
    val weather: LiveData<WeatherEntity> = _weather

    fun loadWeather(city: String) {
        viewModelScope.launch {
            try {
                val result = useCase(city)
                _weather.value = result
            } catch (e: Exception) { }
        }
    }
}