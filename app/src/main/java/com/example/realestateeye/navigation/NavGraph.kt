package com.example.realestateeye.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.realestateeye.viewmodels.RealEstateViewModel
import com.example.realestateeye.views.CameraView
import com.example.realestateeye.views.HomeView
import com.example.realestateeye.views.MapView

@Composable
fun SetupNavGraph(navHostController: NavHostController) {

    val listingViewModel: RealEstateViewModel = viewModel()
    val listings by listingViewModel.listings.observeAsState(initial = emptyList())

    NavHost(
        navController = navHostController,
        startDestination = Screen.HomeScreen.route
    ) {
//        composable(route = Screen.LoginScreen.route) {
//            LoginView(navController = navHostController)
//        }
        composable(route = Screen.HomeScreen.route) {
            HomeView(navController = navHostController)
        }
        composable(route = Screen.MapScreen.route) {
            MapView(listings = listings)
        }
        composable(route = Screen.CameraScreen.route) {
            CameraView()
        }
    }
    listingViewModel.getListings()
}