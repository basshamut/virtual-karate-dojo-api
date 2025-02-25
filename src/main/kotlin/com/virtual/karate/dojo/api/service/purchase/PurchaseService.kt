package com.virtual.karate.dojo.api.service.purchase

import com.virtual.karate.dojo.api.persistance.purchase.Purchases
import com.virtual.karate.dojo.api.persistance.meet.MeetRepository
import com.virtual.karate.dojo.api.persistance.purchase.PurchaseRepository
import com.virtual.karate.dojo.api.persistance.user.UserRepository
import com.virtual.karate.dojo.api.service.MailerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.math.ceil

@Service
class PurchaseService @Autowired constructor(
    private val purchaseRepository: PurchaseRepository,
    private val meetRepository: MeetRepository,
    private val mailerService: MailerService,
    private val mongoTemplate: MongoTemplate,
    private val userRepository: UserRepository
) {
    fun save(purchase: Purchases): Purchases {
        val meet = purchase.meetId?.let {
            meetRepository.findById(it).orElseThrow { IllegalArgumentException("Meet not found") }
        }

        val newPurchase = purchase.copy(
            price = meet?.price,
            purchaseDate = Date(),
            active = true
        )
        val purchaseCreated = purchaseRepository.save(newPurchase)
        mailerService.sendMail(
            to = "user@example.com", // Aquí deberías obtener el email del usuario
            subject = "Compra realizada",
            text = "Has comprado ${meet!!.name} por ${meet.price}",
            html = "<h1>Compra confirmada</h1><p>Has adquirido ${meet.name}</p>",
            attachment = null // Puedes agregar una factura en PDF si es necesario
        )
        return purchaseCreated
    }

    fun getPaginatedPurchases(
        page: Int = 1,
        limit: Int = 10,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null
    ): Map<String, Any> {
        val pageable = PageRequest.of(page - 1, limit)
        val query = Query().with(pageable)

        // Construimos una sola condición para purchaseDate
        val criteriaList = mutableListOf<Criteria>()

        if (startDate != null) {
            val startOfDay = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            criteriaList.add(Criteria.where("purchaseDate").gte(startOfDay))
        }

        if (endDate != null) {
            val endOfDay = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant())
            criteriaList.add(Criteria.where("purchaseDate").lte(endOfDay))
        }

        // Agregar la combinación de criterios si hay fechas definidas
        if (criteriaList.isNotEmpty()) {
            query.addCriteria(Criteria().andOperator(*criteriaList.toTypedArray()))
        }

        val purchases = mongoTemplate.find(query, Purchases::class.java)
        val totalCount = mongoTemplate.count(Query.of(query).limit(0).skip(0), Purchases::class.java)
        val totalPages = ceil(totalCount.toDouble() / limit).toInt()

        val finalPurchase = purchases.mapNotNull { purchase ->
            val meetDate = purchase.meetId?.let {
                meetRepository.findById(it).orElse(null)?.meetDate
            }
            val email = purchase.userId?.let {
                userRepository.findById(it).orElse(null)?.email
            }

            mapOf(
                "id" to purchase.id,
                "userId" to purchase.userId,
                "meetId" to purchase.meetId,
                "purchaseDate" to purchase.purchaseDate,
                "price" to purchase.price,
                "active" to purchase.active,
                "meet" to mapOf("meetDate" to meetDate),
                "user" to mapOf("email" to email)
            )
        }


        return mapOf(
            "totalItems" to totalCount,
            "totalPages" to totalPages,
            "currentPage" to page,
            "itemsPerPage" to limit,
            "hasNextPage" to (page < totalPages),
            "hasPreviousPage" to (page > 1),
            "purchases" to finalPurchase
        )
    }


}
