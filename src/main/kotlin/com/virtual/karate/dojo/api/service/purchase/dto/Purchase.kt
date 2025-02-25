package com.virtual.karate.dojo.api.service.purchase.dto

data class Purchase(
    val productId: String,
    val quantity: Int,
    val price: Double
)
