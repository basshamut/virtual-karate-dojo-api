package com.virtual.karate.dojo.api.controller.user

import com.virtual.karate.dojo.api.persistance.user.Users
import com.virtual.karate.dojo.api.service.user.UserService
import com.virtual.karate.dojo.api.service.user.dto.LoginRequestDto
import com.virtual.karate.dojo.api.service.user.dto.RegisterRequestDto
import com.virtual.karate.dojo.api.service.user.dto.ValidateRequestDto
import com.virtual.karate.dojo.api.utils.Constants.API_VERSION_PATH
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RestController
@RequestMapping("$API_VERSION_PATH/users")
class UserController(private val userService: UserService) {

    private fun validateEmail(email: String?): Boolean {
        return email?.matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+\$")) == true
    }

    private fun validatePassword(password: String?): Boolean {
        return (password?.length ?: 0) >= 8
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequestDto): ResponseEntity<Any> {
        val errors = mutableMapOf<String, String>()

        if (request.user.isNullOrEmpty()) {
            errors["user"] = "El email es requerido"
        } else if (!validateEmail(request.user)) {
            errors["user"] = "El formato del email no es válido"
        }

        val today = Calendar.getInstance()
        today.add(Calendar.YEAR, -18)
        val majorityAgeDate = today.time

        if (request.date.isNullOrEmpty()) {
            errors["date"] = "La fecha de nacimiento es requerida"
        } else {
            val userDate = Date(request.date)
            if (userDate.after(majorityAgeDate)) {
                errors["date"] = "Debes ser mayor de 18 años"
            }
        }

        if (request.password.isNullOrEmpty()) {
            errors["password"] = "La contraseña es requerida"
        } else if (!validatePassword(request.password)) {
            errors["password"] = "La contraseña debe tener al menos 8 caracteres"
        }

        if (errors.isNotEmpty()) {
            return ResponseEntity.badRequest().body(mapOf("errors" to errors))
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = request.date?.let { LocalDate.parse(it, formatter) }
        val birthDay: Date = Date.from(localDate?.atStartOfDay(ZoneId.systemDefault())!!.toInstant())

        val newUser = Users(email = request.user, password = request.password, birthDate = birthDay)
        val userFound = userService.save(newUser)
        return if (userFound == null) {
            ResponseEntity.status(409).body(mapOf("message" to "El email ya está registrado"))
        } else {
            ResponseEntity.status(201).body(mapOf("message" to "Registro exitoso"))
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequestDto): ResponseEntity<Any> {
        val userFound = request.username?.let { userService.findByEmail(it) }

        return if (userFound == null) {
            ResponseEntity.status(404).body(mapOf("message" to "Usuario no encontrado"))
        } else {
            userFound.password = null
            ResponseEntity.ok(userFound)
        }
    }

    @GetMapping("/roles")
    fun getRoles(@RequestParam userMail: String): ResponseEntity<Any> {
        val role = userService.getRole(userMail)
        return ResponseEntity.ok(role)
    }

    @PatchMapping("/validate")
    fun validate(@Valid @RequestBody request: ValidateRequestDto): ResponseEntity<Any> {
        if (request.userMail.isNullOrEmpty()) {
            return ResponseEntity.badRequest().body(mapOf("message" to "El email es requerido"))
        }

        val userExist = userService.findByEmail(request.userMail)
        if (userExist != null) {
            if (userExist.validated == true) {
                return ResponseEntity.badRequest().body(mapOf("message" to "El email ya está validado"))
            }

            userService.validate(request.userMail)
            return ResponseEntity.ok(mapOf("message" to "Validación exitosa"))
        }
        return ResponseEntity.status(404).body(mapOf("message" to "El email no está registrado"))
    }
}