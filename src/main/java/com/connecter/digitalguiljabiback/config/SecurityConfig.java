package com.connecter.digitalguiljabiback.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final String[] whiteList = {
      "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**",
      "/api/login/**",
      "/error",
      "/api/v1/boards/popular", "/api/v1/users/nickname/*/exists"
    };

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf()
          .disable()
          .authorizeHttpRequests()
            .requestMatchers(whiteList).permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/boards/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/boards").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/categories/root").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/categories/*/children").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/categories/*/ancestor").permitAll()
            .requestMatchers( "/api/v1/admin/**").hasAuthority("ADMIN")
            .anyRequest().authenticated()
          .and()
          .authenticationProvider(authenticationProvider)
          .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
