package com.virtual.karate.dojo.api.persistance.meet

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MeetRepository : MongoRepository<Meets, String> {
    fun findByMeetUrl(meetUrl: String): Meets?
    fun findAllByActive(active: Boolean): List<Meets>
    fun findAllByMeetDateBefore(date: Date): List<Meets>
    fun findAllByMeetDateBetween(currentHour: Date?, nextHour: Date?): List<Meets>
}