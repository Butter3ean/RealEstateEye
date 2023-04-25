package com.example.realestateeye.models

data class RealEstateListing(
    val address: Address,
    val coordinates: Coordinates,
    val details: Details,
    val id: Int,
    val mlsNum: Int,
    val urls: Urls
)