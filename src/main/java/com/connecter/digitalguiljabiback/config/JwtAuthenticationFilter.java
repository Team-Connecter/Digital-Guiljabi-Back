package com.connecter.digitalguiljabiback.config;

import com.connecter.digitalguiljabiback.exception.UserLockedException;
import com.connecter.digitalguiljabiback.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {


        final String authHeader;
        authHeader = request.getHeader("Authorization");

        final String jwt;
        final String userId; //oauth uid

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); //Bearer 제외
        userId = jwtService.extractUsername(jwt);

        if (userId == null) {
            throw new JwtException("유효하지 않은 토큰");
        }
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(userId);
            } catch (UsernameNotFoundException e) {
                throw new JwtException("찾을 수 없는 사용자");
            }

            if (jwtService.isTokenValid(jwt, userDetails)) {
                if (!userDetails.isAccountNonLocked()) {
                    throw new UserLockedException();
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null, //우리는 credentials가 없는 사용자 사용
                  userDetails.getAuthorities()
                );

                authToken.setDetails(
                  new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                throw new JwtException("유효하지 않은 토큰");
            }
        }

        filterChain.doFilter(request, response);
    }
}
