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
class SimpleCORSFilter: Filter {

    fun SimpleCORSFilter() {
        val log = LoggerFactory.getLogger(SimpleCORSFilter::class.java)
        log.info("SimpleCORSFilter init")
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val request = req as HttpServletRequest
        val response = res as HttpServletResponse

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type, Accept, X-Requested-With, remember-me")
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition, Authorization")
        chain.doFilter(req, res)
    }

    override fun init(filterConfig: FilterConfig?) {
        // NOOP
    }

    override fun destroy() {
        // NOOP
    }

}