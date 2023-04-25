package com.example.realestateeye.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.realestateeye.models.RealEstateListing
import com.example.realestateeye.viewmodels.RealEstateViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


@SuppressLint("MissingPermission")
@Composable
fun MapView() {

    val listingViewModel: RealEstateViewModel = viewModel()
    val listings by listingViewModel.listings.observeAsState(initial = emptyList())

    val cameraPositionState = rememberCameraPositionState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        MapMarkersWithCustomWindow(listings = listings)
    }

    listingViewModel.getListings()

}




@Composable
fun MapMarkersWithWindow(listings: List<RealEstateListing>) {
    for (listing in listings) {

        val state = MarkerState(
            position = LatLng(
                listing.coordinates.latitude,
                listing.coordinates.longitude
            )
        )

        MarkerInfoWindowContent(
            state = state
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(240.dp)
            ) {
                Column() {
                    AsyncImage(
                        model = listing.urls.imageUrl,
                        contentDescription = "Image of the listing",
                        contentScale = ContentScale.Crop
                    )


                }

            }
        }
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
            )
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
//                    AsyncImage(
//                        model = listing.urls.imageUrl,
//                        contentDescription = "Image of the listing",
//                        contentScale = ContentScale.Crop,
//                    )
                    Row() {
                        Text(text = "Price: " + listing.details.price)
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(text = "Beds: " + listing.details.beds)
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(text = "Baths: " + listing.details.baths)
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(text = "SqFt: " + listing.details.sqFt)
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(text = listing.urls.listingUrl)
                }
            }
        }
    }
}


