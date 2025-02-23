package com.virtual.karate.dojo.api.service.user.dto

data class AuthDto(
    val username: String = "",
    val password: String = "",
    val authorities: Set<String> = emptySet()
)
