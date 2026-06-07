package kakaotech.task4.common.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .version("v1.0.0")
                .title("카테부 개인플젝 API")
                .description("카테부 개인 플젝 API 명세입니다.");

        return new OpenAPI()
                .info(info);
    }
}