package com.ziwei.pomodoro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("紫微番茄钟 API")
                        .version("1.0")
                        .description("紫微番茄钟 · AI个性化学习教练后端接口"));
    }
}
