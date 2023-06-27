package com.dh.keycloakrunner;


import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class KeycloakInitializerRunner implements CommandLineRunner {

    // Keycloak
    private final Keycloak keycloak;
    private static final String KEYCLOAK_SERVER_URL = "http://localhost:8080";

    // Realm
    private static final String REALM_NAME = "TiendaDH";

    // Client
    private static final String CLIENT_ID = "api-gateway-client";
    private static final String CLIENT_SECRET = "yT5xxYtvwCZZDFDJeE3w0oOxmff0uktK";
    private static final String REDIRECT_URL = "http://localhost:8090/*";
    private static final String ROOT_URL = "http://localhost:8090";
    private static final String WEB_ORIGINS = "http://localhost:8090";
    private static final String ADMIN_URL = "http://localhost:8090";

    // Users
    private static final List<UserPass> USERS = Arrays.asList(
            new UserPass("admin", "admin"),
            new UserPass("user", "password"),
            new UserPass("provider", "password"));


    @Override
    public void run(String... args) {
        log.info("Initializing '{}' realm in Keycloak ...", REALM_NAME);

        // Busca si el Realm ya existe y lo elimina
        Optional<RealmRepresentation> representationOptional = keycloak.realms().findAll().stream().filter(r -> r.getRealm().equals(REALM_NAME)).findAny();
        if (representationOptional.isPresent()) {
            log.info("Removing already pre-configured '{}' realm", REALM_NAME);
            keycloak.realm(REALM_NAME).remove();
        }

        // Realm
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(REALM_NAME);
        realmRepresentation.setEnabled(true);
        realmRepresentation.setRegistrationAllowed(true);

        // Client
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(CLIENT_ID);
        clientRepresentation.setSecret(CLIENT_SECRET);
        clientRepresentation.setEnabled(true);
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setServiceAccountsEnabled(true);
        //clientRepresentation.setPublicClient(true);
        clientRepresentation.setRootUrl(ROOT_URL);
        clientRepresentation.setRedirectUris(Collections.singletonList(REDIRECT_URL));
        clientRepresentation.setWebOrigins(Collections.singletonList(WEB_ORIGINS));
        clientRepresentation.setAdminUrl(ADMIN_URL);
        //clientRepresentation.setDefaultRoles(new String[]{WebSecurityConfig.USER});
        realmRepresentation.setClients(Collections.singletonList(clientRepresentation));




        // Users
        List<UserRepresentation> userRepresentations = USERS.stream().map(userPass -> {
            // User Credentials
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            credentialRepresentation.setValue(userPass.getPassword());

            // User
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setUsername(userPass.getUsername());
            userRepresentation.setEnabled(true);
            userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
            //userRepresentation.setClientRoles(getClientRoles(userPass));

            return userRepresentation;
        }).collect(Collectors.toList());
        realmRepresentation.setUsers(userRepresentations);

        // Create Realm
        keycloak.realms().create(realmRepresentation);


        // ---------------------------------------------------------------------------------------------
        // Get real info
        RealmsResource realmsResource = keycloak.realms();
        GroupsResource groupsResource;
        RolesResource rolesResource;
        GroupRepresentation groupRepresentation;
        UserRepresentation userRepresentation;
        RolesRepresentation rolesRepresentation;
        RoleRepresentation roleRepresentation;

        // Creating group "PROVIDERS"
        groupsResource = realmsResource.realm(REALM_NAME).groups();
        groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName("PROVIDERS");
        groupsResource.add(groupRepresentation);

        // Assign group "PROVIDERS" to user "provider"
        userRepresentation = realmsResource.realm(REALM_NAME).users().searchByUsername("provider",true).get(0);
        UserResource userResource = realmsResource.realm(REALM_NAME).users().get(userRepresentation.getId());
        groupsResource = realmsResource.realm(REALM_NAME).groups();
        userResource.joinGroup(groupsResource.groups().get(0).getId());

        // Creating role "USER"
        rolesResource = realmsResource.realm(REALM_NAME).roles();
        roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("USER");
        rolesResource.create(roleRepresentation);


        // Assign role "USER" to user "user"
        UserRepresentation userRepresentation2 = realmsResource.realm(REALM_NAME).users().searchByUsername("user",true).get(0);
        userRepresentation2.setFirstName("userFirstName");
        userRepresentation2.setEmail("user@email.com");

        UserResource userResource2 = realmsResource.realm(REALM_NAME).users().get(userRepresentation2.getId());
        List<RoleRepresentation> roleRepresentationList = userResource2.roles().realmLevel().listEffective();

        rolesResource = realmsResource.realm(REALM_NAME).roles();
        roleRepresentation = rolesResource.get("USER").toRepresentation();
        roleRepresentationList.add(roleRepresentation);

        userResource2.roles().realmLevel().add(roleRepresentationList);


        // Assign user group to generated token
        ClientScopeResource clientScopeResource;
        String scope = "profile";

        List<ClientScopeRepresentation> scopes = realmsResource.realm(REALM_NAME).clientScopes().findAll();
        ClientScopeRepresentation clientScope = scopes.stream()
                .filter(cs -> cs.getName().equals(scope))
                .findFirst()
                .orElse(null);

        clientScopeResource = realmsResource.realm(REALM_NAME).clientScopes().get(clientScope.getId());

        ProtocolMapperRepresentation protocolMapperRepresentation = new ProtocolMapperRepresentation();
        protocolMapperRepresentation.setName("Groups Mapper");
        protocolMapperRepresentation.setProtocol("openid-connect");
        protocolMapperRepresentation.setProtocolMapper("oidc-group-membership-mapper");
        protocolMapperRepresentation.getConfig().put("full.path", "false");
        protocolMapperRepresentation.getConfig().put("access.token.claim", "true");
        protocolMapperRepresentation.getConfig().put("id.token.claim", "true");
        protocolMapperRepresentation.getConfig().put("userinfo.token.claim", "true");
        protocolMapperRepresentation.getConfig().put("claim.name", "group");

        clientScopeResource.getProtocolMappers().createMapper(protocolMapperRepresentation);
        ClientScopeRepresentation clientScopeRepresentation = clientScopeResource.toRepresentation();
        clientScopeResource.update(clientScopeRepresentation);







        // Finish
        log.info("'{}' initialization completed successfully!", REALM_NAME);
    }


    @Value
    private static class UserPass {
        String username;
        String password;
    }
}