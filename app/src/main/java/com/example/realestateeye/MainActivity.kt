package com.example.realestateeye

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.example.realestateeye.ui.theme.RealEstateEyeTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.realestateeye.navigation.SetupNavGraph


class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    // Register the permission request contract
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val cameraGranted = permissions[android.Manifest.permission.CAMERA] == true

        if (fineLocationGranted && coarseLocationGranted && cameraGranted) {
            Log.i("Permissions", "ALL PERMISSIONS GRANTED")
        } else {
            // Some or all permissions denied, handle accordingly
            Log.i("Permissions", "PERMISSIONS DENIED")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealEstateEyeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    permissionLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )

                    navController = rememberNavController()
                    SetupNavGraph(navHostController = navController)
                }
            }
        }
    }
}






