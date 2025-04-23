package com.hectorpolendo.weathertest

import com.hectorpolendo.weathertest.domain.model.WeatherEntity
import com.hectorpolendo.weathertest.domain.repository.WeatherRepository
import com.hectorpolendo.weathertest.domain.usecase.GetWeatherUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetWeatherUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetWeatherUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetWeatherUseCase(repository)
    }

    @Test
    fun `should return weather from repository`() = runTest {
        val expected = WeatherEntity(
            city = "Guadalajara",
            temp = 25.0,
            desc = "Despejado"
        )

        coEvery { repository.getWeather("Guadalajara") } returns expected

        val result = useCase("Guadalajara")

        assert(result == expected)
        coVerify(exactly = 1) { repository.getWeather("Guadalajara") }
    }
}