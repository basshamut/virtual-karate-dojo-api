package com.virtual.karate.dojo.api.controller.meet

import com.cloudinary.Cloudinary
import com.virtual.karate.dojo.api.persistance.meet.Meets
import com.virtual.karate.dojo.api.service.meet.MeetService
import com.virtual.karate.dojo.api.service.meet.dto.ProductDto
import com.virtual.karate.dojo.api.utils.Constants.API_VERSION_PATH
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("$API_VERSION_PATH/meets")
class MeetController(
    private val meetService: MeetService,
    private val cloudinary: Cloudinary
) {

    @PostMapping(consumes = ["multipart/form-data"])
    fun createMeet(
        @RequestPart("meetUrl") meetUrl: String,
        @RequestPart("meetDate") meetDate: String,
        @RequestPart("product", required = true) productJson: String,
        @RequestPart("image") image: MultipartFile
    ): ResponseEntity<Any> {
        return try {
            val dateFormat = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH)
            val meetDateFormatted = dateFormat.parse(meetDate)
            val objectMapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
            val product: ProductDto = objectMapper.readValue(productJson, ProductDto::class.java)
            val imageUrl = uploadImageToCloudinary(image)

            val meet = Meets(
                meetUrl = meetUrl,
                meetDate = meetDateFormatted,
                price = product.price,
                name = product.name,
                description = product.description,
                stripeCode = product.stripeCode,
                imagePath = imageUrl
            )

            val newMeet = meetService.save(meet)
            ResponseEntity.status(HttpStatus.CREATED).body(newMeet)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error al guardar la reunión: ${e.message}"))

        }
    }



    @GetMapping
    fun getMeetByUrl(@RequestParam("url") url: String?): ResponseEntity<Any> {
        if (url.isNullOrEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("message" to "Url parameter is required"))
        }

        val meet = meetService.getByUrl(url)
        return if (meet != null) {
            ResponseEntity.ok(meet)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Meet not found"))
        }
    }

    @GetMapping("/all")
    fun getAllMeets(): ResponseEntity<List<Any>> {
        val meets = meetService.getAll()
        return ResponseEntity.ok(meets)
    }

    private fun uploadImageToCloudinary(image: MultipartFile): String {
        return try {
            val uploadResult = cloudinary.uploader().upload(image.bytes, mapOf("folder" to "karate-classes"))
            uploadResult["secure_url"].toString()
        } catch (e: IOException) {
            throw RuntimeException("Error al subir la imagen a Cloudinary: ${e.message}", e)
        }
    }
}
