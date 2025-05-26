package com.pryabykh.intershop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(withDefaults())
                .anonymous(anonymous -> anonymous
                        .principal("guestUser")
                        .authorities("ROLE_GUEST")
                        .key("uniqueAnonymousKey123")
                )
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers("/create-item-form").hasRole("ADMIN")
                                .pathMatchers("/cart/**").authenticated()
                                .pathMatchers("/orders/**").authenticated()
                                .pathMatchers("/main/items").permitAll()
                                .pathMatchers("/items/**").permitAll()
                                .pathMatchers("/images/**").permitAll()
                                .pathMatchers("/").permitAll()
                                .anyExchange().authenticated()
                )
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
