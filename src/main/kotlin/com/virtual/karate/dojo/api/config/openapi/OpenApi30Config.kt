package com.virtual.karate.dojo.api.config.openapi

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
class OpenApi30Config {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .components(
                Components().addSecuritySchemes(
                    "basicAuth",
                    io.swagger.v3.oas.models.security.SecurityScheme()
                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                        .scheme("basic")
                )
            )
            .info(Info().title("Virtual Karate Dojo API").version("1.0.0"))
            .addSecurityItem(SecurityRequirement().addList("basicAuth"))
    }
}
