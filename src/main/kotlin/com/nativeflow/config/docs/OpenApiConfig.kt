package com.nativeflow.config.docs

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("NativeFlow API")
                    .version("1.0")
                    .description("API do Monolito Modular NativeFlow")
            )
    }

    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("nativeflow-all")
            .packagesToScan("com.nativeflow", "com.content", "com.identity", "com.gamification", "com.learning")
            .build()
    }

    @Bean
    fun contentApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("module-content")
            .packagesToScan("com.content")
            .build()
    }
}