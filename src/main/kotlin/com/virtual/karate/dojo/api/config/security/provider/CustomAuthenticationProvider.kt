package com.virtual.karate.dojo.api.config.security.provider

import com.virtual.karate.dojo.api.service.user.UserService
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class CustomAuthenticationProvider(
    private val passwordEncoder: PasswordEncoder,
    private val cacheManagerLogin: CacheManager,
    private val userService: UserService
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val rawPassword = authentication.credentials as String
        val passwordDecoded = Base64.getDecoder().decode(rawPassword ?: "").toString(Charsets.UTF_8)

        val attempts = cacheManagerLogin.getCache("LOGIN_ATTEMPTS_CACHE")?.get(username)
        checkIfNumberOfPossibleAttemptsReached(attempts)

        val user = userService.loadUserByUsername(username)
            ?: throw UsernameNotFoundException("User not found!")

        if (passwordEncoder.matches(passwordDecoded, user.password)) {
            cacheManagerLogin.getCache("LOGIN_ATTEMPTS_CACHE")?.evict(username)

            val authorities: List<GrantedAuthority> = user.authorities.map { SimpleGrantedAuthority(it) }
            return UsernamePasswordAuthenticationToken(user.username, rawPassword, authorities)
        } else {
            handleFailedAttempt(username, attempts)
        }

        throw BadCredentialsException("Incorrect username or password!")
    }

    private fun handleFailedAttempt(username: String, attempts: Cache.ValueWrapper?) {
        val attemptsValue = (attempts?.get() as? Int) ?: 0
        if (attemptsValue < 5) {
            cacheManagerLogin.getCache("LOGIN_ATTEMPTS_CACHE")?.put(username, attemptsValue + 1)
            throw BadCredentialsException("Incorrect username or password!")
        }
        throw BadCredentialsException("Number of possible attempts reached!")
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }

    companion object {
        private fun checkIfNumberOfPossibleAttemptsReached(attempts: Cache.ValueWrapper?) {
            attempts?.let {
                val attemptsValue = it.get() as Int
                if (attemptsValue >= 5) {
                    throw BadCredentialsException("Number of possible attempts reached!")
                }
            }
        }
    }
}
