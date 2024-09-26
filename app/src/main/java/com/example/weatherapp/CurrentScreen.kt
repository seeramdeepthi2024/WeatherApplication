package com.example.weatherapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.weatherapp.api.Weather
import com.example.weatherapp.api.WeatherModel

@Composable
fun CurrentScreen(
    navController: NavController,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val current by viewModel.current.collectAsState(null)


    Column(
        Modifier
            .fillMaxSize()
    ) {
        current?.let {
            WeatherSummary(weather = it, viewModel)
            weatherDetails(data = it)
        }

    }


}
@Composable
fun WeatherSummary(weather: WeatherModel, viewModel: WeatherViewModel) {
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
            Text(text = data.name, fontSize = 15.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = data.sys.country, fontSize = 15.sp)
        }
        var temp = kelvinToFahrenheit(data.main.temp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = " ${temp} ° F", fontSize = 30.sp,
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
fun kelvinToFahrenheit(kelvin: Double): Double {
    val fahrenheit = (kelvin - 273.15) * 9 / 5 + 32
    return String.format("%.1f", fahrenheit).toDouble()
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
