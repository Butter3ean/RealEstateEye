package com.example.realestateeye.models

data class RealEstateListing(
    val address: Address,
    val coordinates: Coordinates,
    val id: Int,
    val imgUrl: String,
    val listingUrl: String,
    val mlsNum: Int,
    val price: Int
)