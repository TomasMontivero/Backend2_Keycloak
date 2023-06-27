package com.dh.keycloakrunner;


import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
import org.keycloak.common.constants.ServiceAccountConstants;
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


        // -----------------------------------------------------
        // Assign user group to generated token
        ClientScopeResource clientScopeResource;
        String scope = "profile";

        //RealmResource realmResource = keycloakAdmin.realm(realm);
        //List<ClientScopeRepresentation> scopes = realmResource.clientScopes().findAll();
        List<ClientScopeRepresentation> scopes = realmsResource.realm(REALM_NAME).clientScopes().findAll();
        ClientScopeRepresentation clientScope = scopes.stream()
                .filter(cs -> cs.getName().equals(scope))
                .findFirst()
                .orElse(null);

        //String id = clientScope.getId();
        //clientScopeResource = realmsResource.realm(REALM_NAME).clientScopes().get(id);
        clientScopeResource = realmsResource.realm(REALM_NAME).clientScopes().get(clientScope.getId());

        //ProtocolMapperRepresentation groupMembership = new ProtocolMapperRepresentation();
        //groupMembership.setName("group");
        //groupMembership.setProtocol("openid-connect");
        //groupMembership.setProtocolMapper("oidc-group-membership-mapper");
        //groupMembership.getConfig().put("full.path", "false");
        //groupMembership.getConfig().put("access.token.claim", "true");
        //groupMembership.getConfig().put("id.token.claim", "true");
        //groupMembership.getConfig().put("userinfo.token.claim", "true");
        //groupMembership.getConfig().put("claim.name", "groups");
        ProtocolMapperRepresentation protocolMapperRepresentation = new ProtocolMapperRepresentation();
        //protocolMapperRepresentation.setName("group");
        protocolMapperRepresentation.setName("Groups Mapper");
        protocolMapperRepresentation.setProtocol("openid-connect");
        protocolMapperRepresentation.setProtocolMapper("oidc-group-membership-mapper");
        protocolMapperRepresentation.getConfig().put("full.path", "false");
        protocolMapperRepresentation.getConfig().put("access.token.claim", "true");
        protocolMapperRepresentation.getConfig().put("id.token.claim", "true");
        protocolMapperRepresentation.getConfig().put("userinfo.token.claim", "true");
        //protocolMapperRepresentation.getConfig().put("claim.name", "groups");
        protocolMapperRepresentation.getConfig().put("claim.name", "group");

        //ClientScopeResource clientScopeResource = realmResource.clientScopes().get(id);

        //clientScopeResource.getProtocolMappers().createMapper(groupMembership);
        clientScopeResource.getProtocolMappers().createMapper(protocolMapperRepresentation);

        //ClientScopeRepresentation updatedClientScope = clientScopeResource.toRepresentation();
        ClientScopeRepresentation clientScopeRepresentation = clientScopeResource.toRepresentation();
        clientScopeResource.update(clientScopeRepresentation);




        // -----------------------------------------------------
        // TODO: Ver como asignar los roles de esta pagina http://localhost:8080/admin/master/console/#/TiendaDH/clients/e34de373-6ebb-4508-82e4-fb3a5640108f/serviceAccount
        // Set "Service Account Roles" (manage-users,query-users,view-users) for api-gateway-client
        //userRepresentation = realmsResource.realm(REALM_NAME).users().search("api-gateway-client").get(0);  // Usuario del client
        //userResource = realmsResource.realm(REALM_NAME).users().get(userRepresentation.getId());    // Resource del usuario del client
        //rolesResource = realmsResource.realm(REALM_NAME).roles();   // Resource de todos los roles
        //List<RoleRepresentation>  roleRepresentationList1 = userResource.roles().realmLevel().listEffective(); // Lista de roles del usuario

        //System.out.println("---- rolesResource");
        //rolesResource.list().forEach(role -> System.out.println(role.getName()));

        /*
        ////borrador
        ClientResource clientResource;
        //clientResource = realmsResource.realm(REALM_NAME).clients().get("api-gateway-client");
        clientResource = realmsResource.realm(REALM_NAME).clients().get(
                realmsResource.realm(REALM_NAME).clients().findByClientId("api-gateway-client").get(0).getId()
        );
        //System.out.println("---- client id: " + clientResource.toRepresentation().getClientId());
        //System.out.println("---- id: " + clientResource.toRepresentation().getId());
        clientRepresentation = realmsResource.realm(REALM_NAME).clients().findByClientId("api-gateway-client").get(0);
        System.out.println("---- client id: " + clientRepresentation.getClientId());
        System.out.println("---- id: " + clientRepresentation.getId());


        roleRepresentation = new RoleRepresentation("manage-users", "${role_manage-users}",false);
        roleRepresentation.setClientRole(true);
        //roleRepresentation.setContainerId(clientResource.toRepresentation().getId());
        roleRepresentation.setContainerId(clientRepresentation.getId());


        System.out.println("---- roleRepresentation: " + roleRepresentation.getName() + "//" + roleRepresentation.getDescription());



        clientResource
                .roles()
                .create(roleRepresentation);
        roleRepresentation = new RoleRepresentation("query-users", "${role_query-users}",false);
        clientResource.roles().create(roleRepresentation);
        roleRepresentation = new RoleRepresentation("view-users", "${role_view-users}",false);
        clientResource.roles().create(roleRepresentation);
        */






        // Testing
        //UserPass admin = USERS.get(0);
        //log.info("Testing getting token for '{}' ...", admin.getUsername());
        //Keycloak keycloakTesting = KeycloakBuilder.builder().serverUrl(KEYCLOAK_SERVER_URL).realm(REALM_NAME).username(admin.getUsername()).password(admin.getPassword()).clientId(CLIENT_ID).build();
        //log.info("'{}' token: {}", admin.getUsername(), keycloakTesting.tokenManager().grantToken().getToken());
        log.info("'{}' initialization completed successfully!", REALM_NAME);
    }

    /*private Map<String, List<String>> getClientRoles(UserPass userPass) {
        List<String> roles = new ArrayList<>();
        roles.add(WebSecurityConfig.USER);
        if ("admin".equals(userPass.getUsername())) {
            roles.add(WebSecurityConfig.MOVIES_MANAGER);
        }
        return Map.of(CLIENT_ID, roles);
    }*/

    @Value
    private static class UserPass {
        String username;
        String password;
    }
}