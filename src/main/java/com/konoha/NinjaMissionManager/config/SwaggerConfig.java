package com.konoha.NinjaMissionManager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("Ninja Mission Manager API")
                        .description("API para gestionar misiones y ninjas en Konoha")
                        .version("1.0.0"));
    }
}
