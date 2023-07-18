package com.example.realestateeye.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.DMatch
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.Feature2D
import org.opencv.features2d.ORB
import org.opencv.features2d.SIFT
import java.io.File
import java.io.IOException
import java.lang.Integer.max
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

    var imgUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
//            imgUri?.let { compareImagesORB(image = it, listings = listings, context) }
            imgUri?.let { compareImagesSIFT(image = it, listings = listings, context) }
        })

    val progress by animateLottieCompositionAsState(
        composition = comp.value,
        iterations = LottieConstants.IterateForever
    )

    Box(modifier = Modifier.fillMaxSize()) {

        LottieAnimation(
            modifier = Modifier
                .fillMaxSize(),
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

fun compareImagesORB(image: Uri, listings: List<RealEstateListing>, context: Context) {

    val orb = ORB.create()
    val scope = MainScope()

    //convert the uri to a bitmap
    val bitmap = uriToBitmap(image, context)
    //convert the bitmap to a mat
    val baseMat = bitmap?.let { toMat(it) }

    val baseKeyPoints = MatOfKeyPoint()
    val baseDescriptors = Mat()

    orb.detectAndCompute(baseMat, Mat(), baseKeyPoints, baseDescriptors)

    val comparisonTasks = mutableListOf<Deferred<Pair<String, Float>>>()

    for (listing in listings) {
        val task = scope.async {
            val urlBitmap = urlToBitmap(listing.urls.imageUrl)
            val listingMat = urlBitmap?.let { toMat(it) }

            val listingKeypoints = MatOfKeyPoint()
            val listingDescriptors = Mat()
            orb.detectAndCompute(listingMat, Mat(), listingKeypoints, listingDescriptors)

            val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)
            val matches = MatOfDMatch()
            matcher.match(baseDescriptors, listingDescriptors, matches)

            val distSum = matches.toArray().sumByDouble { it.distance.toDouble() }

            val simScore = if(matches.size().height > 0) {
                (distSum / matches.size().height).toFloat()
            } else {
                Float.MAX_VALUE
            }

            listing.urls.listingUrl to simScore
        }
        comparisonTasks.add(task)
    }

    scope.launch {
        val results = comparisonTasks.awaitAll()
        var highestScore = Float.MIN_VALUE
        var highestUrl = ""
        var lowestScore = Float.MAX_VALUE
        var lowestUrl = ""
        for((url, score) in results) {
            Log.d("RESULTS", "URL: $url, SCORE: $score")
            if(score > highestScore) {
                highestScore = score
                highestUrl = url
            }

            if(score < lowestScore) {
                lowestScore = score
                lowestUrl = url
            }
        }

        Log.d("HIGHEST", "HIGHESTURL: $highestUrl, HIGHESTSCORE: $highestScore")
        Log.d("LOWEST", "LOWESTURL: $lowestUrl, LOWESTSCORE: $lowestScore")
    }
}

fun compareImagesSIFT(image: Uri, listings: List<RealEstateListing>, context: Context) {

    val sift = SIFT.create()
    val scope = MainScope()

    //convert the uri to a bitmap
    val bitmap = uriToBitmap(image, context)
    //convert the bitmap to a mat
    val baseMat = bitmap?.let { toMat(it) }

    val baseKeyPoints = MatOfKeyPoint()
    val baseDescriptors = Mat()

    sift.detectAndCompute(baseMat, Mat(), baseKeyPoints, baseDescriptors)

    val comparisonTasks = mutableListOf<Deferred<Pair<String, Float>>>()

    for (listing in listings) {
        val task = scope.async {
            val urlBitmap = urlToBitmap(listing.urls.imageUrl)
            val listingMat = urlBitmap?.let { toMat(it) }

            val listingKeypoints = MatOfKeyPoint()
            val listingDescriptors = Mat()
            sift.detectAndCompute(listingMat, Mat(), listingKeypoints, listingDescriptors)

            val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE)
            val matches = MatOfDMatch()
            matcher.match(baseDescriptors, listingDescriptors, matches)

            val distSum = matches.toArray().sumByDouble { it.distance.toDouble() }

            val simScore = if(matches.size().height > 0) {
                (distSum / matches.size().height).toFloat()
            } else {
                Float.MAX_VALUE
            }

            listing.urls.listingUrl to simScore
        }
        comparisonTasks.add(task)
    }

    scope.launch {
        val results = comparisonTasks.awaitAll()
        var highestScore = Float.MIN_VALUE
        var highestUrl = ""
        var lowestScore = Float.MAX_VALUE
        var lowestUrl = ""
        for((url, score) in results) {
            Log.d("RESULTS", "URL: $url, SCORE: $score")
            if(score > highestScore) {
                highestScore = score
                highestUrl = url
            }

            if(score < lowestScore) {
                lowestScore = score
                lowestUrl = url
            }
        }

        Log.d("HIGHEST", "HIGHESTURL: $highestUrl, HIGHESTSCORE: $highestScore")
        Log.d("LOWEST", "LOWESTURL: $lowestUrl, LOWESTSCORE: $lowestScore")
    }
}


fun uriToBitmap(uri: Uri, context: Context): Bitmap? {

    var bitmap: Bitmap? = null
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream != null) {
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }

    return bitmap
}

suspend fun urlToBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {

    try {
        val imageUrl = URL(url)
        val connection = imageUrl.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val inputStream = connection.inputStream
        val bitmap = BitmapFactory.decodeStream(inputStream)

        inputStream.close()
        connection.disconnect()

        return@withContext bitmap

    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext null

}

fun toMat(bitmap: Bitmap): Mat {
    val mat = Mat(bitmap.height, bitmap.width, CvType.CV_8UC4)
    Utils.bitmapToMat(bitmap, mat)
    return mat
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

