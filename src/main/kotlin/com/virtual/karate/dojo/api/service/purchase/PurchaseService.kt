package com.virtual.karate.dojo.api.service.purchase

import com.virtual.karate.dojo.api.persistance.purchase.Purchases
import com.virtual.karate.dojo.api.persistance.meet.MeetRepository
import com.virtual.karate.dojo.api.persistance.purchase.PurchaseRepository
import com.virtual.karate.dojo.api.service.MailerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.util.*

@Service
class PurchaseService @Autowired constructor(
    private val purchaseRepository: PurchaseRepository,
    private val meetRepository: MeetRepository,
    private val mailerService: MailerService,
    private val mongoTemplate: MongoTemplate
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
        startDate: Date? = null,
        endDate: Date? = null
    ): Map<String, Any> {
        val pageable = PageRequest.of(page - 1, limit)
        val query = Query().with(pageable)

        if (startDate != null) {
            val startOfDay = Calendar.getInstance().apply {
                time = startDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            query.addCriteria(Criteria.where("purchaseDate").gte(startOfDay))
        }

        if (endDate != null) {
            val endOfDay = Calendar.getInstance().apply {
                time = endDate
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time
            query.addCriteria(Criteria.where("purchaseDate").lte(endOfDay))
        }

        val purchases = mongoTemplate.find(query, Purchases::class.java)
        val totalCount = mongoTemplate.count(Query.of(query).limit(0).skip(0), Purchases::class.java)
        val totalPages = Math.ceil(totalCount.toDouble() / limit).toInt()

        return mapOf(
            "totalItems" to totalCount,
            "totalPages" to totalPages,
            "currentPage" to page,
            "itemsPerPage" to limit,
            "purchases" to purchases
        )
    }
}
