package com.project.webApp.config;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
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
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/*", "/main/*", "/users/*", "/films/*", "/img/**").permitAll()
                        .requestMatchers("/users/*/block", "/users/*/unblock", "/films/*/edit", "/films/*/delete", "films/new").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                );
        http.formLogin(AbstractAuthenticationFilterConfigurer::permitAll);
        http.logout(LogoutConfigurer::permitAll);

        return http.build();
    }

}


