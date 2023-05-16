package com.example.realestateeye.views

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.realestateeye.R
import com.example.realestateeye.models.RealEstateListing
import com.example.realestateeye.ui.theme.Blue400
import com.example.realestateeye.viewmodels.RealEstateViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.*

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("MissingPermission")
@Composable
fun MapView() {

    val listingViewModel: RealEstateViewModel = viewModel()
    val listings by listingViewModel.listings.observeAsState(initial = emptyList())

    val cameraPositionState = rememberCameraPositionState()

    val currentCity = remember { mutableStateOf("") }
    val currentCords = remember { mutableStateOf(LatLng(0.0, 0.0)) }

    val context = LocalContext.current

    Box() {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            MapMarkersWithCustomWindows(listings = listings, coords = currentCords)
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            onClick = {
                getCurrentLocation(
                    context,
                    currentCity,
                    currentCords
                ); listingViewModel.getListingsByCity(currentCity.value)
            },
            containerColor = Blue400,
            shape = RoundedCornerShape(32.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location_city),
                contentDescription = null,
                tint = Color.White,
            )
        }


//        Button(
//            onClick = {
//                getCurrentLocation(
//                    context,
//                    currentCity,
//                    currentCords
//                ); listingViewModel.getListingsByCity(currentCity.value)
//            },
//            modifier = Modifier
//                .align(Alignment.BottomStart)
//                .padding(16.dp)
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_location_city),
//                contentDescription = null
//            )
////            Text(text = "Get current city", textAlign = TextAlign.Center)
//        }
    }


    listingViewModel.getListings()
}

//gets the current coordinates of the user
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, city: MutableState<String>, coords: MutableState<LatLng>) {

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation
        .addOnSuccessListener {
            if (it != null) {
                coords.value = LatLng(it.latitude, it.longitude)
                getCurrentCity(context = context, location = it, city)
            }
        }
}

//uses reverse geocoding to get the current city from the users coordinates
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun getCurrentCity(context: Context, location: Location, city: MutableState<String>) {
    val geocoder = Geocoder(context)
    val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    if (address != null) {
        if (address.isNotEmpty()) {
            city.value = address[0]?.locality.toString()
//            Toast.makeText(context, "Location: $city", Toast.LENGTH_SHORT).show()
        }
    }

}

//returns the distance between two sets of coordinates in km
fun haversineDistance(currentCoords: MutableState<LatLng>, otherCoords: LatLng): Double {

    val lat1 = currentCoords.value.latitude
    val lon1 = currentCoords.value.longitude

    val lat2 = otherCoords.latitude
    val lon2 = otherCoords.longitude

    val earthRadius = 6371 // Radius of the Earth in kilometers

    val diffLat = Math.toRadians(lat2 - lat1)
    val diffLon = Math.toRadians(lon2 - lon1)

    val a = sin(diffLat / 2) * sin(diffLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(diffLon / 2) * sin(diffLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c

}

//creates a marker with a markerWindow that shows information about each listing
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MapMarkersWithCustomWindows(listings: List<RealEstateListing>, coords: MutableState<LatLng>) {
    for (listing in listings) {

        val uriHandler = LocalUriHandler.current

        if (haversineDistance(
                coords,
                LatLng(listing.coordinates.latitude, listing.coordinates.longitude)
            ) < 5
        ) {
            MarkerInfoWindow(
                state = MarkerState(
                    position = LatLng(
                        listing.coordinates.latitude,
                        listing.coordinates.longitude
                    )
                ),
                icon = BitmapDescriptorFactory.defaultMarker(202F),
                onInfoWindowLongClick = { uriHandler.openUri(listing.urls.listingUrl) }
            ) {

                Column(
                    modifier = Modifier
                        .size(350.dp)
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(35.dp, 35.dp, 35.dp, 35.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = listing.mlsNum.toString(), modifier = Modifier.padding(8.dp))
                    Box(
                        modifier = Modifier
                            .width(325.dp)
                            .height(235.dp)
                            .padding(12.dp)
                    ) {

                        GlideImage(
                            model = listing.urls.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize()
                                .clip(AbsoluteRoundedCornerShape(25.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Beds: " + listing.details.beds)
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(text = "Baths: " + listing.details.baths)
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Price: " + listing.details.price)
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(text = "SqFt: " + listing.details.sqFt)
                        }
                        Spacer(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

