package com.example.realestateeye.navigation

sealed class Screen(val route: String) {
    object LoginScreen: Screen(route = "login_screen")
    object HomeScreen: Screen(route = "home_screen")
    object MapScreen: Screen(route = "map_screen")
    object CameraScreen: Screen(route = "camera_screen")

}
