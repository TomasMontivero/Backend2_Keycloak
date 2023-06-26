package com.dh.keycloakrunner.config;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${dh.keycloak.serverUrl}")
    private String serverUrl;

    @Value("${dh.keycloak.realm}")
    private String realm;

    @Value("${dh.keycloak.username}")
    private String username;

    @Value("${dh.keycloak.password}")
    private String password;

    @Value("${dh.keycloak.clientId}")
    private String clientId;

    @Bean
    public Keycloak buildClient(){
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientId)
                .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
                .build();
    }

}
