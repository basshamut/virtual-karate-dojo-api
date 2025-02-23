package com.virtual.karate.dojo.api.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.web.servlet.handler.HandlerMappingIntrospector
import com.virtual.karate.dojo.api.config.security.jwt.JWTAuthorizationFilter
import com.virtual.karate.dojo.api.config.security.provider.CustomAuthenticationProvider
import com.virtual.karate.dojo.api.error.exception.MvcRequestMatcherConfigurationException
import com.virtual.karate.dojo.api.utils.Constants.API_VERSION_PATH
import com.virtual.karate.dojo.api.utils.Constants.LOGIN_URL

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val customAuthenticationProvider: CustomAuthenticationProvider
) {
    private val whiteList = arrayOf(
        "/swagger*/**",
        "/v3/api-docs/**",
        "/error",
        LOGIN_URL,
        "${API_VERSION_PATH}/accounts"
    )

    @Bean
    fun securityFilterChain(http: HttpSecurity, introspector: HandlerMappingIntrospector): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                try {
                    whiteList.forEach { pattern ->
                        auth.requestMatchers(MvcRequestMatcher(introspector, pattern)).permitAll()
                    }
                } catch (e: Exception) {
                    throw MvcRequestMatcherConfigurationException("Failed to configure MVC request matchers", e)
                }
                auth.anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilter(getJwtAuthorizationFilter())
        return http.build()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return ProviderManager(listOf(customAuthenticationProvider))
    }

    fun getJwtAuthorizationFilter(): JWTAuthorizationFilter {
        return JWTAuthorizationFilter(authenticationManager())
    }
}

//TODO completar la configuración de seguridad usando un controllador de autenticación (login)