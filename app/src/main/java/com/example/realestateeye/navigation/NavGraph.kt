package com.example.realestateeye.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.realestateeye.views.CameraView
import com.example.realestateeye.views.HomeView
import com.example.realestateeye.views.MapView

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SetupNavGraph(navHostController: NavHostController) {

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
            MapView()
        }
        composable(route = Screen.CameraScreen.route) {
            CameraView()
        }
    }
}