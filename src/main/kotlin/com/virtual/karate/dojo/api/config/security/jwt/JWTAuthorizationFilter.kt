package com.virtual.karate.dojo.api.config.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.virtual.karate.dojo.api.error.exception.ServiceException
import io.jsonwebtoken.Jwts
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import com.virtual.karate.dojo.api.utils.FormatUtils
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import java.util.ArrayList

import com.virtual.karate.dojo.api.utils.Constants.HEADER_AUTHORIZACION_KEY
import com.virtual.karate.dojo.api.utils.Constants.SUPER_SECRET_KEY
import com.virtual.karate.dojo.api.utils.Constants.TOKEN_BEARER_PREFIX

class JWTAuthorizationFilter(authManager: AuthenticationManager) : BasicAuthenticationFilter(authManager) {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader(HEADER_AUTHORIZACION_KEY)
        if (header == null || !header.startsWith(TOKEN_BEARER_PREFIX)) {
            chain.doFilter(req, res)
            return
        }

        try {
            val authentication = getAuthentication(req)
            SecurityContextHolder.getContext().authentication = authentication
            chain.doFilter(req, res)
        } catch (ex: ServiceException) {
            val mapper = ObjectMapper()
            val httpErrorInfoDto = FormatUtils.httpErrorInfoFormatted(HttpStatus.UNAUTHORIZED, req, ex)
            res.contentType = "application/json;charset=UTF-8"
            res.status = HttpStatus.UNAUTHORIZED.value()
            res.writer.write(mapper.writeValueAsString(httpErrorInfoDto))
        }
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        var token = request.getHeader(HEADER_AUTHORIZACION_KEY)
        return if (token != null) {
            token = token.replace(TOKEN_BEARER_PREFIX, "")
            try {
                val user = Jwts.parser()
                    .setSigningKey(SUPER_SECRET_KEY.toByteArray())
                    .parseClaimsJws(token)
                    .body
                    .subject
                if (user != null) {
                    UsernamePasswordAuthenticationToken(user, null, ArrayList())
                } else {
                    null
                }
            } catch (exception: Exception) {
                throw ServiceException("Authentication was not possible: ${exception.message}", 403)
            }
        } else {
            null
        }
    }
}
