package com.example.realestateeye

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.realestateeye.navigation.SetupNavGraph
import com.example.realestateeye.ui.theme.RealEstateEyeTheme
import org.opencv.android.OpenCVLoader


class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    // Register the permission request contract
    private val permissionLaunchers = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted =
            permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted =
            permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val cameraGranted = permissions[android.Manifest.permission.CAMERA] == true
        val writeStorage = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] == true

        if (fineLocationGranted && coarseLocationGranted && cameraGranted && writeStorage) {
            Log.i("Permissions", "ALL PERMISSIONS GRANTED")
        } else {
            // Some or all permissions denied, handle accordingly
            Log.i("Permissions", "PERMISSIONS DENIED")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        if (OpenCVLoader.initDebug()) {
            Log.d("OpenCV Loaded", "success")
        } else {
            Log.d("OpenCV Failed", "failure")
        }
        setContent {
            RealEstateEyeTheme {
                permissionLaunchers.launch(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )

                navController = rememberNavController()
                SetupNavGraph(navHostController = navController)

            }
        }
    }
}






