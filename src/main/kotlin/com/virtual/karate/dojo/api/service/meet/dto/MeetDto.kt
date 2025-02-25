package com.virtual.karate.dojo.api.service.meet.dto

data class MeetDto(
    val meetUrl: String,
    val meetDate: String,
    val product: ProductDto,
    val imagePath: String,
    val active: Boolean = true
)