package com.connecter.digitalguiljabiback.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@OpenAPIDefinition(
  info = @Info(
    title = "디지털길잡이 백엔드 API 명세서",
    description = """
      디지털길잡이 백엔드 API 명세입니다.<br>
      401: 만료된 토큰 or 유효하지 않은 토큰 사용<br>
      500: 서버에러
      """,
    version = "v1"
  )
)
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI(){
    SecurityScheme securityScheme = new SecurityScheme()
      .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
      .in(SecurityScheme.In.HEADER).name("Authorization");
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

    return new OpenAPI()
      .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
      .security(Collections.singletonList(securityRequirement));
  }
}
