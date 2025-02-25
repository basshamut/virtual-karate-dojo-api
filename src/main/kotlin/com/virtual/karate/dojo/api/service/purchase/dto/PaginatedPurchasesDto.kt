package com.virtual.karate.dojo.api.service.purchase.dto

data class PaginatedPurchasesDto(
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int,
    val itemsPerPage: Int,
    val purchases: List<Purchase>
)