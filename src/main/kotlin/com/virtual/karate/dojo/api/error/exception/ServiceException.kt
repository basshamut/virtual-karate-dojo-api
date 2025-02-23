package com.virtual.karate.dojo.api.error.exception

class ServiceException(
    override val message: String,
    val code: Int
) : RuntimeException(message)