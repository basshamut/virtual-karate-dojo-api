package com.virtual.karate.dojo.api.shedule

import com.virtual.karate.dojo.api.persistance.meet.MeetRepository
import com.virtual.karate.dojo.api.persistance.purchase.PurchaseRepository
import com.virtual.karate.dojo.api.persistance.user.UserRepository
import com.virtual.karate.dojo.api.service.MailerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class MeetSchedule @Autowired constructor(
    private val meetRepository: MeetRepository,
    private val purchaseRepository: PurchaseRepository,
    private val mailerService: MailerService,
    private val userRepository: UserRepository
) {
    @Scheduled(cron = "0 0 0 * * *")
    fun deleteOldMeets() {
        try {
            val meets = meetRepository.findAllByMeetDateBefore(Date())
            meets.forEach {
                it.active = false
                meetRepository.save(it)
            }
            println("Se actualizaron ${meets.size} registros.")
        } catch (err: Exception) {
            println("Error al actualizar registros: $err")
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    fun sendMeets() {
        try {
            val now = Calendar.getInstance().apply { timeZone = TimeZone.getTimeZone("GMT") }
            val currentHour = now.time
            now.add(Calendar.HOUR, 1)
            val nextHour = now.time

            val meets = meetRepository.findAllByMeetDateBetween(currentHour, nextHour)
            println("Entre $currentHour y $nextHour se encontraron ${meets.size} registros.")

            meets.forEach { meet ->
                val purchases = purchaseRepository.findAllByMeetId(meet.id!!)
                purchases.forEach { purchase ->
                    val user = userRepository.findById(purchase.userId!!).orElseThrow { IllegalArgumentException("User not found") }
                    mailerService.sendMail(
                        to = user.email!!,
                        subject = "Link: Clase Karate Budo Online",
                        text = "Link para la clase de Karate Budo Online: ${meet.meetUrl}",
                        html = "Link para la clase de Karate Budo Online: ${meet.meetUrl}",
                        attachment = null
                    )
                    println("Notificación enviada a usuario ${purchase.userId}")
                }
                meet.active = false
                meetRepository.save(meet)
            }
        } catch (err: Exception) {
            println("Error al buscar registros: $err")
        }
    }
}
