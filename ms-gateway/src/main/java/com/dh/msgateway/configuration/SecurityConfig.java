package com.dh.msgateway.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    public SecurityWebFilterChain springSecurityFilterChain (ServerHttpSecurity http) {
        http
                .cors().and().csrf().disable()
                .authorizeExchange()
                .anyExchange()
                .authenticated()
                .and()
                .oauth2Login(); // to redirect to oauth2 login page.
        return http.build();
    }

}
