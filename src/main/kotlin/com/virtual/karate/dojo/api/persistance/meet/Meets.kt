package com.virtual.karate.dojo.api.persistance.meet

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("meets")
data class Meets(
    var id: String? = null,
    var meetUrl: String? = null,
    var meetDate: Date? = null,
    var price: Double? = null,
    var name: String? = null,
    var description: String? = null,
    var stripeCode: String? = null,
    var imagePath: String? = null,
    var active: Boolean? = null
)
