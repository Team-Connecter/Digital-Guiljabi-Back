package com.connecter.digitalguiljabiback.config;

import com.connecter.digitalguiljabiback.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(
  securedEnabled = true,
  jsr250Enabled = true)
public class SecurityConfig {

    private final String[] whiteList = {
      "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**",
      "/api/login/**",
      "/error",
      "/api/v1/boards/popular", "/api/v1/users/nickname/*/exists",
      "/api/v1/token/validate", "/api/v1/token/reissue",
      "/logoutPage"
    };

    private final JwtService jwtService;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final AuthenticationProvider authenticationProvider;
    private final UserDetailsService userDetailsService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer()  {
        return (web) -> web.ignoring()
          .requestMatchers(whiteList)
          .requestMatchers(HttpMethod.GET, "/api/v1/boards/*")
          .requestMatchers(HttpMethod.GET, "/api/v1/boards")
          .requestMatchers(HttpMethod.GET, "/api/v1/boards/popular")
          .requestMatchers(HttpMethod.GET, "/api/v1/boards/*/comments")
          .requestMatchers(HttpMethod.GET, "/api/v1/categories/root")
          .requestMatchers(HttpMethod.GET, "/api/v1/categories/*/children")
          .requestMatchers(HttpMethod.GET, "/api/v1/categories/*/ancestor")
          .requestMatchers(HttpMethod.GET, "/api/auth/kakao/login-url")
          .requestMatchers(HttpMethod.GET, "/api/auth/naver/login-url");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf().disable()
          .httpBasic().disable()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
          .cors().configurationSource(corsConfigurationSource())
          .and()
          .authorizeHttpRequests()
            .requestMatchers(whiteList).permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/boards/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/boards").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/boards/popular").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/boards/*/comments").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/categories/root").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/categories/*/children").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/categories/*/ancestor").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/auth/kakao/login-url").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/auth/naver/login-url").permitAll()
            .requestMatchers( "/api/v1/admin/**").hasAuthority("ADMIN")
            .anyRequest().authenticated()
          .and()
          .authenticationProvider(authenticationProvider)
          .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    // CORS 허용 적용
    @Bean //--------- (2)
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost");
        configuration.addAllowedOrigin("http://localhost/");
        configuration.addAllowedOrigin("http://localhost:80");
        configuration.addAllowedOrigin("https://digital.seol.pro");
        configuration.addAllowedOrigin("https://digital.seol.pro/");
        configuration.addAllowedOrigin("https://digital.seol.pro:80");
        configuration.addAllowedOrigin("https://digital.seol.pro:8080");
        configuration.addAllowedOrigin("http://localhost:80/");
        configuration.addAllowedOrigin("https://lucky-llama-7b0801.netlify.app");
        configuration.addAllowedOrigin("https://lucky-llama-7b0801.netlify.app/");
        configuration.addAllowedOrigin("https://lucky-llama-7b0801.netlify.app:443");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
