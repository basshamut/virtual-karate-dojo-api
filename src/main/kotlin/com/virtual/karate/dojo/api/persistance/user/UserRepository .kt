package com.virtual.karate.dojo.api.persistance.user

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<Users, String>{
    fun findByEmail(email: String): Users?
}