package com.virtual.karate.dojo.api.config.stripe

import com.stripe.Stripe
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StripeConfig {
    @Value("\${stripe.secret-key}")
    private lateinit var stripeSecretKey: String

    @Value("\${urls.frontend-url}")
    private lateinit var frontendUrl: String

    @Bean
    fun stripeApiKey(): String {
        Stripe.apiKey = stripeSecretKey
        return stripeSecretKey
    }

    @Bean
    fun frontendUrl(): String {
        return frontendUrl
    }
}
