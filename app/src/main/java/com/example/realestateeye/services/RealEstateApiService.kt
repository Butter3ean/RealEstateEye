package com.example.realestateeye.services

import com.example.realestateeye.models.RealEstateListing
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RealEstateApiService {

    @GET("/api/realestatelistings")
    suspend fun getListings(): Response<List<RealEstateListing>>

    @GET("/api/realestatelistings")
    suspend fun getListingsByCity(@Query("city") city: String): Response<List<RealEstateListing>>
}