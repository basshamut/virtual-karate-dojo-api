package com.virtual.karate.dojo.api.persistance.user

import org.springframework.data.annotation.Id
import java.util.Date

data class Users (
    @Id
    var id: String? = null,
    var email: String? = null,
    var birthDate: Date? = null,
    var password: String? = null,
    var role: String? = null,
    var validated: Boolean? = null,
    var active: Boolean? = null
)