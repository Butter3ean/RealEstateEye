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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.realestateeye.models.RealEstateListing
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage



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
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(listing.listingUrl)
                            .build(),
                        contentDescription = "image of the listing",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = listing.address.toString(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = listing.listingUrl.toString(), textAlign = TextAlign.Center, modifier = Modifier.clickable {
                    })
                }
            }
        }
    }
}
