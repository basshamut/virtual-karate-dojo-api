package com.virtual.karate.dojo.api.config.cors

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.IOException

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SimpleCORSFilter : Filter {

    private val log = LoggerFactory.getLogger(SimpleCORSFilter::class.java)

    init {
        log.info("SimpleCORSFilter initialized")
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val request = req as HttpServletRequest
        val response = res as HttpServletResponse

        val origin = request.getHeader("Origin") ?: "*"

        response.setHeader("Access-Control-Allow-Origin", origin)
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With, remember-me")
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition, Authorization")

        if ("OPTIONS".equals(request.method, ignoreCase = true)) {
            response.status = HttpServletResponse.SC_OK
            return
        }

        chain.doFilter(req, res)
    }

    override fun init(filterConfig: FilterConfig?) {
        // No se necesita implementación
    }

    override fun destroy() {
        // No se necesita implementación
    }
}
