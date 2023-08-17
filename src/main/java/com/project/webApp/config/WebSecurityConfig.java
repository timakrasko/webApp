package com.project.webApp.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
//                        .requestMatchers("/", "/register", "/users", "/confirm").permitAll()
                        .requestMatchers("/*", "/users/*", "/films/*").permitAll()
                        .requestMatchers("/users/*/block", "/users/*/unblock", "/films/*/edit", "/films/*/delete").hasAuthority("ADMIN")
//                        .requestMatchers("/*", "/users/*", "/films/*").hasAnyAuthority("ADMIN", "USER")
                        .anyRequest().authenticated()
                );
        http.formLogin(AbstractAuthenticationFilterConfigurer::permitAll);
        http.logout(LogoutConfigurer::permitAll);

        return http.build();
    }
}


