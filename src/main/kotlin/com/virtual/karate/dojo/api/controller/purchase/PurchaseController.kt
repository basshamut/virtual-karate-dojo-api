package com.virtual.karate.dojo.api.controller.purchase

import com.virtual.karate.dojo.api.persistance.purchase.Purchases
import com.virtual.karate.dojo.api.service.purchase.PurchaseService
import com.virtual.karate.dojo.api.utils.Constants.API_VERSION_PATH
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("$API_VERSION_PATH/purchases")
class PurchaseController(
    private val purchaseService: PurchaseService
) {

    @PostMapping
    fun createPurchase(@RequestBody purchase: Purchases): ResponseEntity<Any> {
        return try {
            val savedPurchase = purchaseService.save(purchase)
            ResponseEntity.ok(savedPurchase)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to e.message))
        }
    }

    @GetMapping
    fun getPurchases(
        @RequestParam(value = "page", required = false, defaultValue = "1") page: Int,
        @RequestParam(value = "limit", required = false, defaultValue = "10") limit: Int,
        @RequestParam(value = "startDate", required = false) startDate: String?,
        @RequestParam(value = "endDate", required = false) endDate: String?
    ): ResponseEntity<Any> {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
            val start: LocalDate? = startDate?.let { LocalDateTime.parse(it, formatter).toLocalDate() }
            val end: LocalDate? = endDate?.let { LocalDateTime.parse(it, formatter).toLocalDate() }
            val paginatedResult = purchaseService.getPaginatedPurchases(page, limit, start, end)

            val paginationInfo = mapOf(
                "totalItems" to paginatedResult["totalItems"],
                "totalPages" to paginatedResult["totalPages"],
                "currentPage" to paginatedResult["currentPage"],
                "itemsPerPage" to paginatedResult["itemsPerPage"],
                "hasNextPage" to paginatedResult["hasNextPage"],
                "hasPreviousPage" to paginatedResult["hasPreviousPage"]
            )

            ResponseEntity.ok(mapOf("paginationInfo" to paginationInfo, "data" to paginatedResult["purchases"]))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to e.message))
        }
    }
}