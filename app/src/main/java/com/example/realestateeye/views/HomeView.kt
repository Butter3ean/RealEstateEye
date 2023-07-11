package com.example.realestateeye.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
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
import com.example.realestateeye.ui.theme.Blue400
import com.example.realestateeye.viewmodels.RealEstateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.ORB
import org.opencv.features2d.SIFT
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeView(navController: NavController, listingViewModel: RealEstateViewModel = viewModel()) {

    val listings by listingViewModel.listings.observeAsState(initial = emptyList())
    listingViewModel.getListings()

    val context = LocalContext.current
    val comp = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ani_background))

    var hasImage by remember { mutableStateOf(false) }
    var imgUri by remember { mutableStateOf<Uri?>(null) }


//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture(),
//        onResult = { success -> hasImage = success })

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->

//            imgUri?.let { compareImagesORB(it, listings, context) }
//            imgUri?.let { compareImagesSURF(it, listings, context) }
            imgUri?.let { compareImagesSIFT(it, listings, context) }
        })

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
                    val file = createImageFile(context = context)
                    imgUri =
                        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    cameraLauncher.launch(imgUri)
//                    val uri = ComposeFileProvider.getImageUri(context = context)
//                    imgUri = uri
//                    cameraLauncher.launch(uri)


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

}

//fun compareImagesSURF(img: Uri, listings: List<RealEstateListing>, context: Context) {
//    var highestMatch = ""
//    var highestScore = 0.0
//
//    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, img)
//    val img1 = Mat(bitmap.height, bitmap.width, CvType.CV_8UC1)
//    Utils.bitmapToMat(bitmap, img1)
//
//    val keypoints1 = MatOfKeyPoint()
//    val descriptors1 = Mat()
//
//    val surf = SURF.create()
//    val surf = SURF
//
//    surf.detectAndCompute(img1, Mat(), keypoints1, descriptors1)
//    val matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED)
//
//    val scope = MainScope()
//
//    for (listing in listings) {
//        scope.launch {
//            val img2Bitmap = loadBitmapFromUrl(listing.urls.imageUrl)
//            val img2 = img2Bitmap?.let { Mat(it.height, img2Bitmap.width, CvType.CV_8UC1) }
//            Utils.bitmapToMat(img2Bitmap, img2)
//
//            val keypoints2 = MatOfKeyPoint()
//            val descriptors2 = Mat()
//            surf.detectAndCompute(img2, Mat(), keypoints2, descriptors2)
//
//            val matches = MatOfDMatch()
//            matcher.match(descriptors1, descriptors2, matches)
//
//            val score = matches.toArray().size.toDouble()
//
//            if (score > highestScore) {
//                highestMatch = listing.urls.listingUrl
//                highestScore = score
//            }
//
//            Log.d("SCORES", "highestScore: $highestScore")
//            Log.d("URLS", "url: $highestMatch")
//        }
//    }
//}


fun compareImagesSIFT(img: Uri, listings: List<RealEstateListing>, context: Context) {
    var highestMatch = ""
    var highestScore = 0.0

    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, img)
    val img1 = Mat(bitmap.height, bitmap.width, CvType.CV_8UC1)
    Utils.bitmapToMat(bitmap, img1)

    val keypoints1 = MatOfKeyPoint()
    val descriptors1 = Mat()

    val sift = SIFT.create()

    sift.detectAndCompute(img1, Mat(), keypoints1, descriptors1)
    val matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED)

    val scope = MainScope()

    for (listing in listings) {
        scope.launch {
            val img2Bitmap = loadBitmapFromUrl(listing.urls.imageUrl)
            val img2 = img2Bitmap?.let { Mat(it.height, img2Bitmap.width, CvType.CV_8UC1) }
            Utils.bitmapToMat(img2Bitmap, img2)

            val keypoints2 = MatOfKeyPoint()
            val descriptors2 = Mat()
            sift.detectAndCompute(img2, Mat(), keypoints2, descriptors2)

            val matches = MatOfDMatch()
            matcher.match(descriptors1, descriptors2, matches)

            val score = matches.toArray().size.toDouble()

            if (score > highestScore) {
                highestMatch = listing.urls.listingUrl
                highestScore = score
            }

            Log.d("SCORES", "highestScore: $highestScore")
            Log.d("URLS", "url: $highestMatch")
        }
    }
}


fun compareImagesORB(img: Uri, listings: List<RealEstateListing>, context: Context) {

    var highestMatch = ""
    var highestScore = 0.0


    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, img)
    val img1 = Mat(bitmap.height, bitmap.width, CvType.CV_8UC1)
    Utils.bitmapToMat(bitmap, img1)

    val keypoints1 = MatOfKeyPoint()
    val descriptors1 = Mat()

    val orb = ORB.create()


    orb.detectAndCompute(img1, Mat(), keypoints1, descriptors1)
    val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)

    val scope = MainScope()

    for (listing in listings) {

        scope.launch {
            val img2Bitmap = loadBitmapFromUrl(listing.urls.imageUrl)
            val img2 = img2Bitmap?.let { Mat(it.height, img2Bitmap.width, CvType.CV_8UC1) }
            Utils.bitmapToMat(img2Bitmap, img2)

            val keypoint2 = MatOfKeyPoint()
            val descriptors2 = Mat()
            orb.detectAndCompute(img2, Mat(), keypoint2, descriptors2)

            val matches = MatOfDMatch()
            matcher.match(descriptors1, descriptors2, matches)

            val score = matches.toArray().size.toDouble()

            if (score > highestScore) {
                highestMatch = listing.urls.listingUrl
                highestScore = score
            }

            Log.d("SCORES", "highestScore: $highestScore")
            Log.d("URLS", "url: $highestMatch")
        }

    }

}

suspend fun loadBitmapFromUrl(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
    var bitmap: Bitmap? = null
    try {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val inputStream = connection.inputStream
        bitmap = BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    bitmap
}


fun createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /*prefix*/
        ".jpg", /*suffix*/
        storageDir /*directory*/
    )
}

