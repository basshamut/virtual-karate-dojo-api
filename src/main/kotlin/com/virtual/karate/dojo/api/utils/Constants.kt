package com.virtual.karate.dojo.api.utils

object Constants {
    const val API_VERSION_PATH = "/v1"

    // Auth
    const val LOGIN_PATH = "/login"
    const val LOGIN_URL = "$API_VERSION_PATH$LOGIN_PATH"

    // Cache
    const val LOGIN_ATTEMPTS_CACHE = "loginAttempts"

    // JWT
    const val ISSUER_INFO = "space-api"
    const val SUPER_SECRET_KEY = "spaceapi5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437"
    const val TOKEN_EXPIRATION_TIME_IN_MINUTES = 1440 // One day
    const val HEADER_AUTHORIZACION_KEY = "Authorization"
    const val TOKEN_BEARER_PREFIX = "Bearer "
}