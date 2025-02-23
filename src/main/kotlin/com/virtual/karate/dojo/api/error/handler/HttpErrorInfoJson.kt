package com.virtual.karate.dojo.api.error.handler

import org.springframework.http.HttpStatus
import java.time.ZonedDateTime

data class HttpErrorInfoJson(
    val timestamp: String = ZonedDateTime.now().toString(),
    val path: String,
    val httpStatus: HttpStatus,
    val message: String
)
