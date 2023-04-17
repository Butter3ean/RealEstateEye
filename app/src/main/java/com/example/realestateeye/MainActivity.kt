package com.example.realestateeye

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.example.realestateeye.ui.theme.RealEstateEyeTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.realestateeye.navigation.SetupNavGraph


class MainActivity : ComponentActivity() {

    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealEstateEyeTheme {
                navController = rememberNavController()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupNavGraph(navHostController = navController)
                }
            }
        }
    }
}





