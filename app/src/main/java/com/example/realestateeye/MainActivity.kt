package com.example.realestateeye

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Patterns
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.core.text.util.LinkifyCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.realestateeye.models.RealEstateListing
import com.example.realestateeye.ui.theme.RealEstateEyeTheme
import com.example.realestateeye.viewmodels.RealEstateViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


class MainActivity : ComponentActivity() {

    private val listingViewModel: RealEstateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealEstateEyeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val listings by listingViewModel.listings.observeAsState(initial = emptyList())
                    Map(listings = listings)
                }

                listingViewModel.getListings()
            }
        }
    }
}

@Composable
fun Map(listings: List<RealEstateListing>) {

    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        MapMarkersWithCustomWindow(listings = listings)

    }
}

//@Composable
//fun MapMarkers(listings: List<RealEstateListing>) {
//    for(listing in listings) {
//        Marker(
//            state = MarkerState(position = LatLng(listing.coordinates.latitude, listing.coordinates.longitude)),
//            title = listing.mlsNum.toString(),
//            snippet = listing.listingUrl
//        )
//    }
//}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MapMarkersWithCustomWindow(listings: List<RealEstateListing>) {
    for (listing in listings) {
//        Marker(
//            state = MarkerState(position = LatLng(listing.coordinates.latitude, listing.coordinates.longitude)),
//            title = listing.mlsNum.toString(),
//            snippet = listing.listingUrl
//        )
        MarkerInfoWindow(
            state = MarkerState(
                position = LatLng(
                    listing.coordinates.latitude,
                    listing.coordinates.longitude
                )
            ),
        ) {
            Box(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(35.dp, 35.dp, 35.dp, 35.dp)
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlideImage(
                        model = listing.imgUrl,
                        contentDescription = "Image of listing",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(160.dp)
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = listing.address.toString(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    DefaultLinkifyText(text = listing.listingUrl)

                }
            }
        }
    }
}

@Composable
fun DefaultLinkifyText(modifier: Modifier = Modifier, text: String?) {
    val context = LocalContext.current
    val customLinkifyTextView = remember {
        TextView(context)
    }
    AndroidView(modifier = modifier, factory = { customLinkifyTextView }) { textView ->
        textView.text = text ?: ""
        LinkifyCompat.addLinks(textView, Linkify.ALL)
        Linkify.addLinks(textView, Patterns.PHONE,"tel:",
            Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter)
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}

@Composable
fun ClickableHyperLink(hyperLink: String) {
    val annotatedLinkString = buildAnnotatedString {
        val str = "Click here to view all information about this listing"
        val startIndex = 0;
        val endIndex = str.lastIndex
        append(str)
        addStyle(
            style = SpanStyle(
                color = Color(0xff64B5F6),
                textDecoration = TextDecoration.Underline
            ), start = startIndex, end = endIndex
        )
        addStringAnnotation(
            tag = "URL",
            annotation = hyperLink,
            start = startIndex,
            end = endIndex
        )
    }

    val uriHandler = LocalUriHandler.current
    ClickableText(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        text = annotatedLinkString,
        onClick = {
            annotatedLinkString.getStringAnnotations("URL", it, it).firstOrNull()
                ?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }

        }
    )
}

