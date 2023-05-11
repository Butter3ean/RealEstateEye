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

class RealEstateViewModel: ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://real-estate-eye-api.herokuapp.com")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()

    private val listingApiService = retrofit.create(RealEstateApiService::class.java)
    private val _listings = MutableLiveData<List<RealEstateListing>>()
    val listings: LiveData<List<RealEstateListing>> get() = _listings

    fun getListings() {
        viewModelScope.launch {
            try {
                val response = listingApiService.getListings()
                if(response.isSuccessful) {
                    _listings.value = response.body()
                }
            } catch(_: java.lang.Exception) {

            }
        }
    }

    fun getListingsByCity() {
        viewModelScope.launch {
            try {
                val response = listingApiService.getListingsByCity("Savannah")
                if(response.isSuccessful) {
                    _listings.value = response.body()
                }
            } catch (_: java.lang.Exception) {

            }
        }
    }


}