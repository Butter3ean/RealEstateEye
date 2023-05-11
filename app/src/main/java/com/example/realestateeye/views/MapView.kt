package com.example.realestateeye.views

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.realestateeye.models.RealEstateListing
import com.example.realestateeye.viewmodels.RealEstateViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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

    listingViewModel.getListingsByCity()

}

//@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MapMarkersWithCustomWindow(listings: List<RealEstateListing>) {

    for (listing in listings) {

        val painter = rememberAsyncImagePainter(listing.urls.listingUrl)

        if(painter.state is AsyncImagePainter.State.Success) {
            MarkerInfoWindow(
                state = MarkerState(
                    position = LatLng(
                        listing.coordinates.latitude,
                        listing.coordinates.longitude
                    )
                ),
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
            ) {
                Column(
                    modifier = Modifier
                        .size(450.dp)
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(35.dp, 35.dp, 35.dp, 35.dp)
                        )
                ) {

                    Image(painter = painter, contentDescription = null)

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


//
//@Composable
//fun MapMarkersWithCustomWindow(listings: List<RealEstateListing>) {
//    for (listing in listings) {
//        MarkerInfoWindowContent(
//            state = MarkerState(
//                position = LatLng(
//                    listing.coordinates.latitude,
//                    listing.coordinates.longitude
//                )
//            ),
//            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Box(modifier = Modifier.size(200.dp)) {
//                    SubcomposeAsyncImage(
//                        model = ImageRequest.Builder(LocalContext.current)
//                            .data(listing.urls.imageUrl)
//                            .crossfade(true)
//                            .build(),
//                        loading = {
//                            CircularProgressIndicator()
//                        },
//                        contentDescription = "Image of the listing",
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .aspectRatio(0.5f)
//                            .scale(0.5f, 0.5f)
//                    )
//                }
//                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
//                    Row(modifier = Modifier.fillMaxWidth()) {
//                        Text(text = "Beds: " + listing.details.beds)
//                        Spacer(modifier = Modifier.weight(1f))
//                        Text(text = "Baths: " + listing.details.baths)
//                    }
//                    Spacer(modifier = Modifier.padding(8.dp))
//                    Row(modifier = Modifier.fillMaxWidth()) {
//                        Text(text = "Price: " + listing.details.price)
//                        Spacer(modifier = Modifier.weight(1f))
//                        Text(text = "SqFt: " + listing.details.sqFt)
//                    }
//                }
//                Spacer(modifier = Modifier.padding(8.dp))
//                Text(text = listing.urls.listingUrl)
//            }
//        }
//    }
//}
//COIL Image
//                    SubcomposeAsyncImage(
//                        model = ImageRequest.Builder(context) // Use the passed-in context
//                            .data(listing.urls.imageUrl)
//                            .crossfade(true)
//                            .build(),
//                        loading = {
//                            CircularProgressIndicator()
//                        },
//                        contentDescription = "Image of the listing",
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .aspectRatio(0.5f)
//                            .scale(0.5f, 0.5f)
//                    )

