package com.example.realestateeye.views

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.realestateeye.R
import com.example.realestateeye.navigation.Screen
import com.example.realestateeye.ui.theme.Blue400
import com.example.realestateeye.ui.theme.Blue600

fun openCameraApp(context: Context) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    context.startActivity(intent)
}

@Composable
fun HomeView(navController: NavController) {

    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Blue600)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo_color),
                contentDescription = "Logo of app"
            )

            Button(
                onClick = { navController.navigate(route = Screen.MapScreen.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                colors = ButtonDefaults.buttonColors(Blue400)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_maps),
                    contentDescription = "Icon of a map"
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(text = "Check what's nearby")
            }

            Button(
                onClick = { openCameraApp(context = context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                colors = ButtonDefaults.buttonColors(Blue400)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Icon of a camera"
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(text = "Capture an Image")
            }
//          Button(
//                onClick = { navController.navigate(route = Screen.CameraScreen.route) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(15.dp),
//                colors = ButtonDefaults.buttonColors(Blue400)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_camera),
//                    contentDescription = "Icon of a camera"
//                )
//                Spacer(modifier = Modifier.width(width = 8.dp))
//                Text(text = "Capture an Image")
//            }
        }
    }
}



