package com.example.realestateeye.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.realestateeye.LocationManager
import com.example.realestateeye.R
import com.example.realestateeye.models.RealEstateListing
import com.example.realestateeye.ui.theme.Blue400
import com.example.realestateeye.viewmodels.RealEstateViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import haversineDistance
import java.text.DecimalFormat
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("MissingPermission")
@Composable
fun MapView(listingViewModel: RealEstateViewModel = viewModel()) {

    val listings by listingViewModel.listings.observeAsState(initial = emptyList())
    val context = LocalContext.current

    val currentCity = remember { mutableStateOf("") }
    val currentCords = remember { mutableStateOf(LatLng(0.0, 0.0)) }

    val locationManager = LocationManager()
    locationManager.getCurrentLocation(context, currentCity, currentCords)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentCords.value, 5f)
    }

    var value by remember { mutableStateOf("5") }
    var loaded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            MapAppBar(
                context,
                value,
                locationManager,
                listingViewModel,
                currentCity,
                currentCords
            ) { newValue ->
                value = newValue
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            ListingMap(
                cameraPositionState = cameraPositionState,
                listings = listings,
                coordinates = currentCords,
                value
            )
        }
    }
}

@Composable
fun MapAppBar(
    context: Context,
    value: String,
    locationManager: LocationManager,
    viewModel: RealEstateViewModel,
    city: MutableState<String>,
    coordinates: MutableState<LatLng>,
    onValueChange: (String) -> Unit
) {
    BottomAppBar(containerColor = Blue400) {
        Row(modifier = Modifier.fillMaxSize()) {
            DistanceTextField(value, onValueChange)
            GetListingsButton(locationManager, context, viewModel, city, coordinates)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistanceTextField(value: String, onValueChange: (String) -> Unit) {

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = "Enter max distance") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.padding(4.dp, 8.dp, 18.dp, 0.dp),
        colors = TextFieldDefaults.textFieldColors(containerColor = Color.White)
    )
}

@Composable
fun GetListingsButton(
    locationManager: LocationManager,
    context: Context,
    viewModel: RealEstateViewModel,
    city: MutableState<String>,
    coordinates: MutableState<LatLng>
) {
    Button(
        onClick = {
            locationManager.getCurrentLocation(
                context,
                city,
                coordinates
            ); viewModel.getListingsByCity(city.value)
        },
        modifier = Modifier.padding(0.dp, 10.dp)
    ) {
        Text(text = "GO")
    }
}

@Composable
fun ListingMap(
    cameraPositionState: CameraPositionState,
    listings: List<RealEstateListing>,
    coordinates: MutableState<LatLng>,
    value: String
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        MapMarkersWithCustomWindows(listings = listings, coords = coordinates, value = value)
    }
}

//creates a marker with a markerWindow that shows information about each listing
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MapMarkersWithCustomWindows(
    listings: List<RealEstateListing>,
    coords: MutableState<LatLng>,
    value: String,
) {
    val formatter = DecimalFormat("#,###")
    val distance = value.trim().toIntOrNull()

    if (distance != null) {

        for (listing in listings) {

            val uriHandler = LocalUriHandler.current

            if (haversineDistance(
                    coords,
                    LatLng(listing.coordinates.latitude, listing.coordinates.longitude)
                ) < distance
            ) {
                MarkerInfoWindow(
                    state = MarkerState(
                        position = LatLng(
                            listing.coordinates.latitude,
                            listing.coordinates.longitude
                        )
                    ),
                    icon = BitmapDescriptorFactory.defaultMarker(202F),
                    onInfoWindowLongClick = { uriHandler.openUri(listing.urls.listingUrl) },
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
                        Text(
                            text = "$${formatter.format(listing.details.price)}",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.cabin)),
                            fontSize = 30.sp
                        )
                        Text(
                            text = "${listing.address.street}, " +
                                    "${listing.address.city}, " +
                                    "${listing.address.state} " +
                                    listing.address.zipCode,
                            fontFamily = FontFamily(Font(R.font.cabin))
                        )
                        Box(
                            modifier = Modifier
                                .width(325.dp)
                                .height(235.dp)
                                .padding(12.dp)
                        ) {

                           val img = Glide.with(LocalContext.current)
                                .load(listing.urls.imageUrl)
                                .preload()

                            GlideImage(
                                model = listing.urls.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(AbsoluteRoundedCornerShape(25.dp)),
                                contentScale = ContentScale.Crop,
                            )

                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_bed),
                                    contentDescription = null
                                )
                                Text(
                                    text = listing.details.beds.toString(),
                                    fontFamily = FontFamily(Font(R.font.cabin))
                                )
                                Spacer(modifier = Modifier.padding(16.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_bath),
                                    contentDescription = null
                                )
                                Text(
                                    text = listing.details.baths.toString(),
                                    fontFamily = FontFamily(Font(R.font.cabin))
                                )
                                Spacer(modifier = Modifier.padding(16.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_sqft),
                                    contentDescription = null
                                )
                                Text(
                                    text = formatter.format(listing.details.sqFt),
                                    fontFamily = FontFamily(Font(R.font.cabin))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



