package com.virtual.karate.dojo.api.controller.stripe

import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import com.virtual.karate.dojo.api.service.meet.MeetService
import com.virtual.karate.dojo.api.utils.Constants.API_VERSION_PATH
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$API_VERSION_PATH/stripe")
class StripeController @Autowired constructor(
    private val meetService: MeetService,
    private val frontendUrl: String
) {
    @PostMapping("/create-checkout-session")
    fun createCheckoutSession(@RequestBody request: Map<String, String>): ResponseEntity<Map<String, String>> {
        return try {
            val meetId = request["meetId"] ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "meetId is required"))
            val userId = request["userId"] ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "userId is required"))

            val meet = meetService.getOne(meetId) ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Meet not found"))

            val sessionParams = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setPaymentIntentData(
                    SessionCreateParams.PaymentIntentData.builder()
                        .build()
                )
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setPrice(meet.stripeCode)
                        .setQuantity(1)
                        .build()
                )
                .setSuccessUrl("$frontendUrl/dojo/dashboard?state=succeeded&meetId=$meetId&userId=$userId")
                .setCancelUrl("$frontendUrl/dojo/dashboard?state=canceled&meetId=$meetId&userId=$userId")
                .build()

            val session = Session.create(sessionParams)
            ResponseEntity.ok(mapOf("sessionId" to session.id))
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to e.message.toString()))
        }
    }

}
