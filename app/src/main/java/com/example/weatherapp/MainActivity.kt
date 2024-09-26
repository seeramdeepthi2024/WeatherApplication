package com.example.weatherapp

import android.os.Bundle
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.MaterialTheme
import coil.compose.AsyncImage
import com.example.weatherapp.api.Weather

import com.example.weatherapp.api.WeatherModel
import com.example.weatherapp.ui.theme.WeatherAppTheme

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
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

    @Composable
    fun MainScreen(viewModel: WeatherViewModel) {

        val navController = rememberNavController()
        val topBarTitle = remember {
            mutableStateOf(NavigationItem.CurrentWeather.title)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopBar(topBarTitle.value) },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Navigation(navController = navController)
                }
            },
            bottomBar = {
                BottomNavigationBar(navController){
                    topBarTitle.value = it
                }
            }
        )
    }

    @Composable
    fun TopBar(title : String) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onBackground
                )
            },
            contentColor = MaterialTheme.colors.onBackground
        )
    }

    @Composable
    fun BottomNavigationBar(navController: NavController, onValueChange : (String) -> Unit) {
        val items = listOf(
            NavigationItem.CurrentWeather,
            NavigationItem.Forecasting
        )

        BottomNavigation(
            contentColor = MaterialTheme.colors.onBackground
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                BottomNavigationItem(

                    label = { Text(text = item.title) },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            modifier = Modifier
                                .height(20.dp)
                                .width(20.dp),
                            contentDescription = item.title
                        )
                    },
                    alwaysShowLabel = true,
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }

                            launchSingleTop = true
                            restoreState = true
                            onValueChange(item.title)
                        }
                    }
                )
            }
        }


    }

    /*@OptIn(ExperimentalMaterial3Api::class)
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

    }*/

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
