package com.virtual.karate.dojo.api.persistance.purchase

import org.springframework.data.annotation.Id
import java.util.*

data class Purchases(
    @Id
    var id: String? = null,
    var userId: String? = null,
    var meetId: String? = null,
    var purchaseDate: Date? = null,
    var price: Double? = null,
    var active: Boolean? = null
)
