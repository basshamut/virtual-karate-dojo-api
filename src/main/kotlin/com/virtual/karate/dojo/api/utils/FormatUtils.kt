package com.virtual.karate.dojo.api.utils

import com.virtual.karate.dojo.api.error.handler.HttpErrorInfoJson
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

object FormatUtils {
    private val log = LoggerFactory.getLogger(FormatUtils::class.java)

    fun httpErrorInfoFormatted(status: HttpStatus, request: HttpServletRequest, ex: Exception): HttpErrorInfoJson {
        val path = request.requestURI
        val message = ex.message ?: "Unknown error"
        log.debug("Returning HttpStatus: {} for path: {}, message: {}", status, path, message)
        return HttpErrorInfoJson(httpStatus = status, path = path, message = message)
    }
}