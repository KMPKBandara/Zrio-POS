package com.zrio.pos.config;

import com.zrio.pos.user.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SessionAuthenticationStrategy
    sessionAuthenticationStrategy() {

        return new ChangeSessionIdAuthenticationStrategy();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {

        HttpSessionCsrfTokenRepository repository =
                new HttpSessionCsrfTokenRepository();

        repository.setHeaderName("X-CSRF-TOKEN");

        return repository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository,
            CsrfTokenRepository csrfTokenRepository,
            SessionAuthenticationStrategy sessionAuthenticationStrategy
    ) throws Exception {

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                )

                .securityContext(context -> context
                        .securityContextRepository(
                                securityContextRepository
                        )
                        .requireExplicitSave(true)
                )

                .sessionManagement(session -> session
                        .sessionAuthenticationStrategy(
                                sessionAuthenticationStrategy
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/actuator/health",
                                "/api/system/**",
                                "/api/auth/csrf",
                                "/api/auth/login"
                        ).permitAll()

                        .requestMatchers(
                                "/api/users/**"
                        ).hasRole("OWNER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories"
                        ).hasAnyRole(
                                "OWNER",
                                "CASHIER"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories/all"
                        ).hasRole("OWNER")

                        .requestMatchers(
                                "/api/categories",
                                "/api/categories/**"
                        ).hasRole("OWNER")

                        .requestMatchers(
                                "/api/auth/me",
                                "/api/auth/logout"
                        ).authenticated()

                        .anyRequest().authenticated()
                )

                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(401)
                        )

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) ->
                                        response.sendError(403)
                        )
                );

        return http.build();
    }
}