package com.tera.pretest.core.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info()
                .title("PreTest")
                .version("1.0.0")
                .description("PreTest API documentation")
        );
    }

    @Bean(name = "cpuMonitoringSwagger")
    public GroupedOpenApi cpuMonitoring(){
        return GroupedOpenApi.builder()
                .group("CPU Monitoring Stats Search")
                .pathsToMatch("/monitoring/**")
                .build();
    }

}
