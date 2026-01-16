package com.nativeflow.config.docs

import com.common.constansts.SecurityConstants
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val securitySchemeName = "cookieAuth"
        val cookieName = SecurityConstants.AUTH_COOKIE_NAME
        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .name(cookieName)
                            .type(SecurityScheme.Type.APIKEY)
                            .`in`(SecurityScheme.In.COOKIE)
                    )
            )
            .addSecurityItem(
                SecurityRequirement()
                    .addList(securitySchemeName)
            )
            .info(
                Info()
                    .title("NativeFlow API")
                    .version("1.0")
                    .description("NativeFlow Restful API")
            )
    }

    @Bean
    fun publicApi(): GroupedOpenApi = GroupedOpenApi.builder()
            .group("nativeflow-all")
            .packagesToScan("com.nativeflow", "com.content", "com.identity", "com.gamification", "com.learning")
            .build()

    @Bean
    fun contentApi(): GroupedOpenApi = GroupedOpenApi.builder()
            .group("module-content")
            .packagesToScan("com.content")
            .build()

    @Bean
    fun identityApi(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("module-identity")
        .packagesToScan("com.identity")
        .build()

    @Bean
    fun gamificationApi(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("module-gamification")
        .packagesToScan("com.gamification")
        .build()

    @Bean
    fun learningApi(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("module-learning")
        .packagesToScan("com.learning")
        .build()
}