package com.example.realestateeye.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.realestateeye.models.RealEstateListing
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*



@Composable
fun MapView(listings: List<RealEstateListing>) {

    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        MapMarkersWithCustomWindow(listings = listings)

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MapMarkersWithCustomWindow(listings: List<RealEstateListing>) {
    for (listing in listings) {
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
//                    DefaultLinkifyText(text = listing.listingUrl)
                    Text(text = listing.listingUrl.toString(), textAlign = TextAlign.Center, modifier = Modifier.clickable {
//                        OpenUrl(uri = listing.listingUrl)

                    })
                }
            }
        }
    }
}