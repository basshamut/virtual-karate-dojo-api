package com.virtual.karate.dojo.api.config.security.basic

import com.fasterxml.jackson.databind.ObjectMapper
import com.virtual.karate.dojo.api.error.exception.ServiceException
import com.virtual.karate.dojo.api.utils.FormatUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import java.util.*

class BasicAuthFilter(
    private val authManager: AuthenticationManager
) : BasicAuthenticationFilter(authManager) {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val authHeader = req.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            chain.doFilter(req, res)
            return
        }

        try {
            val authentication = getAuthentication(authHeader)
            val authenticatedUser = authManager.authenticate(authentication) // Delegar autenticación

            SecurityContextHolder.getContext().authentication = authenticatedUser
            chain.doFilter(req, res)
        } catch (ex: ServiceException) {
            val mapper = ObjectMapper()
            val httpErrorInfoDto = FormatUtils.httpErrorInfoFormatted(HttpStatus.UNAUTHORIZED, req, ex)
            res.contentType = "application/json;charset=UTF-8"
            res.status = HttpStatus.UNAUTHORIZED.value()
            res.writer.write(mapper.writeValueAsString(httpErrorInfoDto))
        }
    }

    private fun getAuthentication(authHeader: String): Authentication {
        val base64Credentials = authHeader.substringAfter("Basic ").trim()
        val credentialsDecoded = String(Base64.getDecoder().decode(base64Credentials))
        val (username, password) = credentialsDecoded.split(":", limit = 2)

        return UsernamePasswordAuthenticationToken(username, password)
    }
}
