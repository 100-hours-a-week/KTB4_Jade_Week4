package kakaotech.task4.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kakaotech.task4.common.security.SecurityPaths;
import kakaotech.task4.common.security.csrf.CsrfTokenHandler;
import kakaotech.task4.common.security.filter.JwtAuthenticationFilter;
import kakaotech.task4.common.security.filter.JwtExceptionFilter;
import kakaotech.task4.common.security.handler.JwtAuthenticationEntryPoint;
import kakaotech.task4.common.security.handler.SecurityAccessDeniedHandler;
import kakaotech.task4.common.security.token.JwtAuthService;
import kakaotech.task4.common.security.properties.CookieProperties;
import kakaotech.task4.common.security.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthService jwtAuthService;
    private final ObjectMapper objectMapper;
    private final CookieProperties cookieProperties;
    private final CorsProperties corsProperties;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final SecurityAccessDeniedHandler securityAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtExceptionFilter jwtExceptionFilter = new JwtExceptionFilter(objectMapper);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtAuthService, cookieProperties);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository())
                        .csrfTokenRequestHandler(new CsrfTokenHandler())
                        .ignoringRequestMatchers(SecurityPaths.SIGN_UP, SecurityPaths.SIGN_IN, SecurityPaths.TOKEN_REISSUE)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(securityAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityPaths.PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtExceptionFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, JwtExceptionFilter.class);

        return http.build();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(this::customizeCsrfCookie);
        return repository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(corsProperties.allowedOrigins());
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of(HttpHeaders.CONTENT_TYPE, "X-XSRF-TOKEN"));
            configuration.setAllowCredentials(true);
            return configuration;
        };
    }

    private void customizeCsrfCookie(ResponseCookie.ResponseCookieBuilder cookie) {
        cookie.path("/");
        cookie.secure(cookieProperties.secure());
        cookie.sameSite(cookieProperties.sameSite());
    }
}