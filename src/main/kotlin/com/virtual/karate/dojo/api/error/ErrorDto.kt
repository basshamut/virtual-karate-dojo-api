package com.virtual.karate.dojo.api.error

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ErrorDto {
    private val errorMessage = ""
}