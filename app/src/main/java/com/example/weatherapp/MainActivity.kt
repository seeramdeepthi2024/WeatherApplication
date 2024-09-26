package com.example.weatherapp

import android.os.Bundle
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.api.Weather

import com.example.weatherapp.api.WeatherModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.theme.WeatherViewModel

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherAppTheme {
                val permission = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

                PermissionRequired(
                    permissionState = permission,
                    permissionNotGrantedContent = {LocationPermissionDetails(onContinueClick = permission::launchPermissionRequest)} ,
                    permissionNotAvailableContent = {LocationPermissionNotAvailable(onContinueClick = permission::launchPermissionRequest)}
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(viewModel: WeatherViewModel) {

        val current by viewModel.current.collectAsState(null)


        Column(
            Modifier
                .fillMaxSize()
        ) {
            current?.let {
                WeatherSummary(weather = it)
                weatherDetails(data = it)
            }

        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun WeatherSummary(weather: WeatherModel) {
        val selectedCityWeather by viewModel.selectedCityWeather.collectAsState()

        var city by remember{
            mutableStateOf("")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = city, onValueChange = {
                        city = it
                    },
                    label = {
                        Text(text = "Search for any location")
                    })
                IconButton(onClick = {
                    viewModel.updateSelectedCity(city)
                    viewModel.fetchSelectedCityWeather(city)}) {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = "Search For any location")
                }

            }
            selectedCityWeather?.let{
                weatherDetails(data = it)
            }
        }

    }
    @Composable
    fun weatherDetails(data: WeatherModel){

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom)
            {
                Icon(imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location icon",
                    modifier = Modifier.size(40.dp)
                )
                Text(text = data.name, fontSize = 30.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = data.sys.country, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = " ${data.main.temp} ° K", fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            horizontalImageListScreen(data.weather)

            Text(text = data.weather.get(0).main,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun horizontalImageListScreen(weatherConditions: List<Weather>) {
        LazyRow {
            items(weatherConditions) { eachWeatherCondition ->
                var imageUrl = "https://openweathermap.org/img/wn/${eachWeatherCondition.icon}.png"
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Image loaded from Coil",
                    modifier = Modifier.size(100.dp) // Adjust size as needed
                )
            }
        }
    }

}
