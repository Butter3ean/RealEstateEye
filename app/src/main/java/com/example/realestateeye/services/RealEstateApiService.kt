package com.example.realestateeye.services

import com.example.realestateeye.models.RealEstateListing
import retrofit2.Response
import retrofit2.http.GET

interface RealEstateApiService {

    @GET("realestatelistings")
    suspend fun getListings(): Response<List<RealEstateListing>>
}