package com.example.weatherapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun ForecastingScreen(
    navController: NavController,
    viewModel: WeatherViewModel = hiltViewModel()
) {

    val selectedCityWeather by viewModel.selectedCityWeather.collectAsState(null)

    selectedCityWeather?.let {

        Column(
            Modifier.fillMaxSize().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom)
            {
                Icon(imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location icon",
                    modifier = Modifier.size(40.dp)
                )
                Text(text = it.name, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = it.sys.country, fontSize = 15.sp)
            }
            Card {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        var feels_like = kelvinToFahrenheit(it.main.feels_like)
                        WeatherKeyVal("Humidity", it.main.humidity.toString())
                        WeatherKeyVal("Feels Like", feels_like.toString())
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        var min_temp = kelvinToFahrenheit(it.main.temp_min)
                        var max_temp = kelvinToFahrenheit(it.main.temp_max)
                        WeatherKeyVal("Min Temp", min_temp.toString())
                        WeatherKeyVal("Max Temp",max_temp.toString() )
                    }

                }
            }


        }

    }

}
@Composable
fun WeatherKeyVal(key : String, value : String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = key, fontWeight = FontWeight.SemiBold, color = Color.Gray)
    }
}