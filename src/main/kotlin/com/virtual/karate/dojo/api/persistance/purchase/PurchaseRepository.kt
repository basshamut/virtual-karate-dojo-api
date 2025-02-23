package com.virtual.karate.dojo.api.persistance.purchase

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseRepository : MongoRepository<Purchases, String> {
    fun findAllByMeetId(id: String): List<Purchases>
}