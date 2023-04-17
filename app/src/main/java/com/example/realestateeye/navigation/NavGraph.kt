package com.example.realestateeye.navigation

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.realestateeye.viewmodels.RealEstateViewModel
import com.example.realestateeye.views.HomeView
import com.example.realestateeye.views.LoginView
import com.example.realestateeye.views.MapView

@Composable
fun SetupNavGraph(navHostController: NavHostController) {

    val listingViewModel: RealEstateViewModel = viewModel()
    val listings by listingViewModel.listings.observeAsState(initial = emptyList())

    NavHost(
        navController = navHostController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(route = Screen.LoginScreen.route) {
            LoginView(navController = navHostController)
        }
        composable(route = Screen.HomeScreen.route) {
            HomeView()
        }
        composable(route = Screen.MapScreen.route) {
            MapView(listings = listings)
        }
    }
    listingViewModel.getListings()
}