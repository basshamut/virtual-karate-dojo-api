package com.virtual.karate.dojo.api.service.user

import com.virtual.karate.dojo.api.persistance.user.UserRepository
import com.virtual.karate.dojo.api.persistance.user.Users
import com.virtual.karate.dojo.api.service.MailerService
import com.virtual.karate.dojo.api.service.user.dto.AuthDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val mailerService: MailerService,
    private val passwordEncoder: PasswordEncoder
) {
    fun save(user: Users): Users? {
        if (user.email?.let { userRepository.findByEmail(it) } == null) {
            val hashedPassword = passwordEncoder.encode(user.password)
            val createdUser = userRepository.save(
                user.copy(
                    password = hashedPassword,
                    birthDate = user.birthDate?.let { Date(it.time) }
                )
            )

            val domain = System.getenv("FRONTEND_URL") ?: "http://localhost"
            val to = user.email
            val subject = "Welcome to Mushin Dojo"
            val text = "Welcome to Mushin Dojo"
            val html = """
                <h1>Welcome to Mushin Dojo</h1>
                <p>
                    Thank you for signing up, please verify your registration by clicking on this link:
                    <a href="$domain/dojo/validation?mail=${user.email}">Dojo</a>
                </p>
            """.trimIndent()

            try {
                to?.let { mailerService.sendMail(it, subject, text, html, null) }
                return createdUser
            } catch (error: Exception) {
                userRepository.delete(createdUser)
                return null
            }
        }
        return null
    }

    fun login(email: String, password: String): Users? {
        val user = userRepository.findByEmail(email) ?: return null
        if (!user.validated!! || !passwordEncoder.matches(password, user.password)) return null
        return user
    }

    fun deleteByEmail(email: String) {
        userRepository.findByEmail(email)?.let { userRepository.delete(it) }
    }

    fun getRole(email: String): String? {
        return userRepository.findByEmail(email)?.role
    }

    fun validate(email: String): Users? {
        val user = userRepository.findByEmail(email) ?: return null
        return userRepository.save(user.copy(validated = true))
    }

    fun findByEmail(email: String): Users? {
        return userRepository.findByEmail(email)
    }

    fun loadUserByUsername(username: String?): AuthDto? {
        val authorities = mutableSetOf<String>()

        val user = username?.let { userRepository.findByEmail(it) }

        return user?.run {
            authorities.add("ROLE_${role ?: "USER"}")
            val passwordDecoded = Base64.getDecoder().decode(password ?: "").toString(Charsets.UTF_8)

            AuthDto(email ?: "", passwordDecoded, authorities)
        }
    }
}
