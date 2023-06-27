package com.example.realestateeye.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestateeye.models.RealEstateListing
import com.example.realestateeye.services.RealEstateApiService
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RealEstateViewModel : ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://real-estate-eye-api.herokuapp.com")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()

    private val listingApiService = retrofit.create(RealEstateApiService::class.java)

    private val _listings = MutableLiveData<List<RealEstateListing>>()
    val listings: LiveData<List<RealEstateListing>> get() = _listings

//    private val _coordinates = MutableLiveData<LatLng>()
//    val coordinates: LiveData<LatLng> get() = _coordinates
//
//    private val _city = MutableLiveData<String>()
//    val city: LiveData<String> get() = _city


    fun getListings() {
        viewModelScope.launch {
            try {
                val response = listingApiService.getListings()
                if (response.isSuccessful) {
                    _listings.value = response.body()
                }
            } catch (_: java.lang.Exception) {

            }
        }
    }


    fun getListingsByCity(city: String) {
        viewModelScope.launch {
            try {
                val response = listingApiService.getListingsByCity(city)
                if (response.isSuccessful) {
                    _listings.value = response.body()
                }
            } catch (_: java.lang.Exception) {

            }
        }
    }


}