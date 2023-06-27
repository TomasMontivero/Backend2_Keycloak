# Backend2_Keycloak
Especialización en Back End II - TP Final - Integracion de microservicios con autenticacion en Keycloak

# BE2 - Documentación de proyecto final TiendaDH

- Repositorio publico:  https://github.com/TomasMontivero/Backend2_Keycloak
- Documentacion Online con screenshots: https://docs.google.com/document/d/1HNHBPKj6dJN83vNEhh6NtcOSWPIVFVj7KW4t5M4Ib74/edit?usp=sharing


# Ejecución del proyecto

1) Ejecutar el Keycloak Runner para crear el realm TiendaDH.
   - El runner va a buscar Keycloak en el puerto 8080, y usa user:admin / password:admin 
   - Si se está levantando Keycloak en otro puerto o con otras credenciales, hay que modificar el application.properties:
     - *keycloak-runner/src/main/resources/application.properties*
   - Ir al microservicio keycloak-runner y ejecutar el KeycloakRunnerApplication
     - *keycloak-runner/src/main/java/com/dh/keycloakrunner/KeycloakRunnerApplication.java*
   - Seteo manual necesario para terminar de configurarlo porque no se pudo implementar en el runner:
     - *http://localhost:8080/admin/master/console/#/TiendaDH/clients*
     - Ir a Clients y entrar al api-gateway-client
     - Ir a la pestaña de Service Accounts Roles y asignar los roles “manage-users”, “query-users” y “view-users”

    
2) Ejecutar Eureka
   - Ir al microservicio ms-discovery y ejecutar el MsDiscoveryApplication
     - *ms-discovery/src/main/java/com/dh/msdiscovery/MsDiscoveryApplication.java*


3) Ejecutar Gateway
   - Ir al microservicio ms-gateway y ejecutar el MsGatewayAplication
     - *ms-gateway/src/main/java/com/dh/msgateway/MsGatewayApplication.java*


4) Ejecutar Bills
   - Ir al microservicio ms-bills y ejecutar el MsBillsApplication
     - *ms-bills/src/main/java/com/dh/msbills/MsBillsApplication.java*


5) Ejecutar Users
   - Ir al microservicio ms-users y ejecutar el MsUsersApplication
     - *ms-users/src/main/java/com/dh/msusers/MsUsersApplication.java*


# Pruebas en Postman


1) Configuración inicial
   - Importar la collection en Postman
   - Todos los request a Users y Bills tienen implementado el “Generate New Access Token” en la pestaña de Authorization
   - Seteo manual necesario:
     - En los 2 GET de Users, es necesario insertar en la URL el ID del user “user” que se generó en Keycloak


2) Haciendo click en  “Generate New Access Token” se debe abrir una ventana de navegador para iniciar sesion en Keycloak.
Usar las credenciales: user: user // password:password

   
3) Users - Get user
   - El endpoint /users/{id} devuelve la informacion del usuario que obtiene de Keycloak

    
4) Users - Get user and bills (feign)
   - El endpoint /users/bills/{id} devuelve la misma informacion del usuario y adiciona las facturas que obtiene de Bills
   - OBSERVACIÓN:
     - Después de unos minutos de levantar el ms-users, el endpoint empieza a devolver permanentemente un 401 al comunicarse con msbills
     Incluso generando un nuevo token, internamente parece no renovarlo. No logré encontrar en el código por que falla.
     - SOLUCIÓN: Apagar y volver a iniciar el microservicio de Users desde IntelliJ, esperar un minuto y volver a probar el endpoint.


5) Bills - Get all bills
   - El endpoint /bills/all devuelve la lista completa de facturas que obtiene de su base de datos

    
6) Bills - Get bills by user
   - El endpoint /bills/findBy/{customerBill} devuelve las facturas que haya encontrado para el usuario pasado por url
   

7) Bills - Save bill
   - El endpoint /bills recibe por body el Json de una factura y la guarda en la base de datos.
   - Borrando cookies de postman y generando un nuevo access token con las credenciales: user: provider // password: password
   - El endpoint devuelve 200 y un Json con la misma factura que se guardó. Porque el usuario provider pertenece al grupo “PROVIDERS”
   - Si se usa el POST con el token del usuario “user” (password: password) el endpoint devuelve 403 porque el usuario no pertenece al grupo “PROVIDERS
