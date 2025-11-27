package com.virtual.karate.dojo.api.config.security

import com.virtual.karate.dojo.api.config.security.basic.BasicAuthFilter
import com.virtual.karate.dojo.api.config.security.provider.CustomAuthenticationProvider
import com.virtual.karate.dojo.api.error.exception.MvcRequestMatcherConfigurationException
import com.virtual.karate.dojo.api.service.user.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

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
        "/actuator/**",
        "/api/v1/users/login",
        "/api/v1/users/register",
        "/api/v1/users/validate"
    )

    @Bean
    fun securityFilterChain(http: HttpSecurity, introspector: HandlerMappingIntrospector): SecurityFilterChain {
        http
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
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
            .addFilterBefore(BasicAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return ProviderManager(listOf(customAuthenticationProvider))
    }
}
