package com.example.realestateeye.views

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality.Companion.High
import androidx.compose.ui.input.key.Key.Companion.D
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.realestateeye.R
import com.example.realestateeye.models.RealEstateListing
import com.example.realestateeye.navigation.Screen
import com.example.realestateeye.providers.ComposeFileProvider
import com.example.realestateeye.ui.theme.Blue400
import com.example.realestateeye.viewmodels.RealEstateViewModel
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.FastFeatureDetector
import org.opencv.features2d.ORB
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.osgi.OpenCVInterface
import kotlin.math.log

@Composable
fun HomeView(navController: NavController, listingViewModel: RealEstateViewModel = viewModel()) {

    val listings by listingViewModel.listings.observeAsState(initial = emptyList())

    val context = LocalContext.current
    val comp = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ani_background))

    var hasImage by remember { mutableStateOf(false) }
    var imgUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> hasImage = success })

    val progress by animateLottieCompositionAsState(
        composition = comp.value,
        iterations = LottieConstants.IterateForever
    )

    Box(modifier = Modifier.fillMaxSize()) {

        LottieAnimation(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            composition = comp.value,
            progress = { progress })

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,

            ) {
            Image(
                painter = painterResource(id = R.drawable.realestateeye_low_resolution_logo_white_on_transparent_background),
                contentDescription = null
            )

            Spacer(modifier = Modifier.padding(128.dp))

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
                onClick = {
                    val uri = ComposeFileProvider.getImageUri(context = context)
                    imgUri = uri
                    cameraLauncher.launch(uri)
                },
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
        }
    }

    if(hasImage && imgUri != null) {
        compareImages(context, imgUri!!, listings)
    }
}

fun compareImages(context: Context, img: Uri, listings: List<RealEstateListing>) {

    var highestMatch = ""
    var highestScore = 0.0

    val imgUri = ComposeFileProvider.getImageUri(context = context)
    val img1 = Imgcodecs.imread(imgUri.toString(), Imgcodecs.IMREAD_GRAYSCALE)
    val keypoints1 = MatOfKeyPoint()
    val descriptors1 = Mat()

    val orb = ORB.create()

    orb.detectAndCompute(img1, Mat(), keypoints1, descriptors1)

    val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)

    for(listing in listings) {
        val img2 = Imgcodecs.imread(listing.urls.imageUrl, Imgcodecs.IMREAD_GRAYSCALE)
        val keypoint2 = MatOfKeyPoint()
        val descriptors2 = Mat()
        orb.detectAndCompute(img2, Mat(), keypoint2, descriptors2)

        val matches = MatOfDMatch()
        matcher.match(descriptors1, descriptors2, matches)

        val score = matches.toArray().size.toDouble()

        if(score > highestScore) {
            highestMatch = listing.urls.listingUrl
            highestScore = score
        }

        Log.d("SCORES", "highestScore: $highestScore")
        Log.d("URLS", "url: $highestMatch")

    }


}



