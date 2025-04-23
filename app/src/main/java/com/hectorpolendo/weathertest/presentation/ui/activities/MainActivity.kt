package com.hectorpolendo.weathertest.presentation.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hectorpolendo.weathertest.R
import com.hectorpolendo.weathertest.data.datasource.local.WeatherLocalDataSource
import com.hectorpolendo.weathertest.data.datasource.remote.WeatherRemoteDataSource
import com.hectorpolendo.weathertest.data.repository.WeatherRepositoryImpl
import com.hectorpolendo.weathertest.databinding.ActivityMainBinding
import com.hectorpolendo.weathertest.domain.usecase.GetWeatherUseCase
import com.hectorpolendo.weathertest.extensions.hideKeyboard
import com.hectorpolendo.weathertest.extensions.showToast
import com.hectorpolendo.weathertest.presentation.viewmodel.WeatherViewModel
import com.hectorpolendo.weathertest.presentation.viewmodel.WeatherViewModelFactory
import com.hectorpolendo.weathertest.utils.RetrofitInstance
import java.util.Locale
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var map: GoogleMap

    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val api = RetrofitInstance.api
        val remoteDataSource = WeatherRemoteDataSource(api, "3bf0c3f98c56bd5c27e55321182c74c5")
        val localDataSource = WeatherLocalDataSource(applicationContext)
        val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
        val useCase = GetWeatherUseCase(repository)
        val factory = WeatherViewModelFactory(useCase)

        viewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)

        binding.btnSearch.setOnClickListener {
            binding.searchEditText.hideKeyboard()
            val city = binding.searchEditText.text.toString().trim()
            if (city.isNotEmpty()) {
                viewModel.loadWeather(city)
                moveMapToCity(city)
            }
        }
    }

    private fun setDefaultCityFromLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (!addresses.isNullOrEmpty()) {
                            val city = addresses[0].locality ?: addresses[0].subAdminArea ?: getString(R.string.unknown_city)
                            viewModel.loadWeather(city)
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(location.latitude, location.longitude),
                                    12f
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableUserLocation()
        setDefaultCityFromLocation()
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true

            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            enableUserLocation()
        }
    }

    private fun moveMapToCity(cityName: String) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(cityName, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)

                map.clear()
                map.addMarker(MarkerOptions().position(latLng).title(cityName))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            } else {
                showToast(getString(R.string.not_found))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}