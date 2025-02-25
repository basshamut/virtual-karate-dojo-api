package com.virtual.karate.dojo.api.service.meet

import com.virtual.karate.dojo.api.persistance.meet.Meets
import com.virtual.karate.dojo.api.persistance.meet.MeetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class MeetService @Autowired constructor(
    private val meetRepository: MeetRepository
) {
    fun save(meet: Meets): Meets {
        if (meet.meetDate?.before(Date.from(Instant.now())) == true) {
            throw IllegalArgumentException("Fecha no proporcionada o inválida")
        }
        val newMeet = meet.copy(active = true)
        return meetRepository.save(newMeet)
    }

    fun getAll(): List<Meets> {
        return meetRepository.findAllByActive(true)
    }

    fun getOne(id: String): Meets? {
        return meetRepository.findById(id).orElse(null)
    }

    fun update(id: String, meet: Meets): Meets? {
        return if (meetRepository.existsById(id)) {
            meetRepository.save(meet.copy(id = id))
        } else null
    }

    fun getByUrl(url: String): Meets? {
        return meetRepository.findByMeetUrl(url)
    }
}