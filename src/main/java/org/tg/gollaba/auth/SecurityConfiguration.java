package org.tg.gollaba.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.tg.gollaba.auth.service.OAuth2AuthenticationFailureHandler;
import org.tg.gollaba.auth.service.OAuth2AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final OAuth2AuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler customAuthenticationFailureHandler;
    private final JwtTokenFilter jwtTokenFilter;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource())
            )
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize())
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(configurer ->
                configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(configurer ->
                configurer.authenticationEntryPoint(new AuthenticationEntryPoint())
            )
            .oauth2Login(configurer ->
                configurer
                    .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint
                            .baseUri("/oauth2/authorize")
                            .authorizationRequestRepository(authorizationRequestRepository)
                    )
                    .userInfoEndpoint(userInfoEndpoint ->
                        userInfoEndpoint
                            .userService(new DefaultOAuth2UserService())
                    )
                    .successHandler(customAuthenticationSuccessHandler)
                    .failureHandler(customAuthenticationFailureHandler)
                );

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        var corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(List.of(
            "http://localhost:5500",
            "http://localhost:3000",
            "https://gollaba-application-deploy.vercel.app",
            "https://gollaba-fe.vercel.app",
            "https://dev.gollaba.app",
            "https://www.gollaba.app"
        ));
        corsConfiguration.setAllowedHeaders(
            List.of(
                "Access-Control-Allow-Headers",
                "Origin",
                "Accept",
                "X-Requested-With",
                "Content-Type",
                "Access-Control-Request-Method",
                "Access-control-allow-credentials",
                "Authorization"
            )
        );
        corsConfiguration.setAllowedMethods(
            List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.OPTIONS.name()
            )
        );
        corsConfiguration.setAllowCredentials(true);

        var corsConfigurationSource = new UrlBasedCorsConfigurationSource();

        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return corsConfigurationSource;
    }

    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorize() {
        return auth -> auth
//            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//            .requestMatchers(HttpMethod.POST, "/v2/users").permitAll()
//            .requestMatchers(HttpMethod.POST, "/api-docs/gollaba").permitAll()
//            .requestMatchers(HttpMethod.GET, "/test").permitAll()
//            .requestMatchers(HttpMethod.GET, "/login/oauth2/**").permitAll()
//            .requestMatchers(HttpMethod.POST, "/login/oauth2/**").permitAll()
//            .requestMatchers(HttpMethod.GET, "/api-docs/**").permitAll()
//            .requestMatchers(HttpMethod.GET, "/v2/api-docs").permitAll()
//            .requestMatchers(HttpMethod.GET, "/v2/swagger-ui/**").permitAll()
//            .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
//            .requestMatchers(HttpMethod.POST, "/v2/auth/renew-token").permitAll()
//            .requestMatchers(HttpMethod.POST, "/v2/auth/make-token").permitAll()
            .anyRequest().permitAll();
    }

    private static class AuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
        public AuthenticationEntryPoint()  {
            super("");
        }
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
                response.sendError(401, "Unauthorized");
                return;
            }

            response.sendError(response.getStatus());
        }
    }
}