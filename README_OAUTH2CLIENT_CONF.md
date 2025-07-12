## Varios clientes-proveedores en una misma aplicación

Sí, esta aplicación puede ser cliente de varios proveedores OAuth2 y tiene mucho sentido en escenarios donde quieres permitir a los usuarios autenticarse usando diferentes servicios (por ejemplo, Google, Facebook, tu propio Authorization Server, etc.).

Para hacerlo, solo debes agregar más bloques bajo `registration` y `provider`, uno por cada proveedor. Por ejemplo:

````yaml
spring:
  security:
    oauth2:
      client:
        registration:
          my-client:
            client-id: my-client-id
            client-secret: my-client-secret
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: openid,profile,email
          google:
            client-id: tu-google-client-id
            client-secret: tu-google-client-secret
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: openid,profile,email
        provider:
          my-provider:
            authorization-uri: http://127.0.0.1:9000/oauth2/authorize
            token-uri: http://127.0.0.1:9000/oauth2/token
            user-info-uri: http://127.0.0.1:9000/userinfo
            jwk-set-uri: http://127.0.0.1:9000/oauth2/jwks
            issuer-uri: http://127.0.0.1:9000
          google:
            authorization-uri: https://accounts.google.com/o/oauth2/v2/auth
            token-uri: https://oauth2.googleapis.com/token
            user-info-uri: https://openidconnect.googleapis.com/v1/userinfo
            jwk-set-uri: https://www.googleapis.com/oauth2/v3/certs
            issuer-uri: https://accounts.google.com
````

**Ventajas:**  
- Permites a los usuarios elegir cómo autenticarse.
- Puedes integrar autenticación corporativa y social en la misma app.

**Spring Security** mostrará automáticamente las opciones de login para cada proveedor configurado.

---

## Redirigir usuario no autenticado al login del servidor de Autorización

La línea:

```java
.oauth2Login(oauth2 -> oauth2
    .loginPage("/oauth2/authorization/oidc-client"))
```

hace que, cuando un usuario no autenticado intente acceder a un recurso protegido, Spring Security lo redirija directamente a la página de inicio de sesión de OAuth2 para el cliente con `registrationId` igual a `oidc-client`.

En otras palabras, fuerza a que el login se haga usando ese proveedor OAuth2 específico, en vez de mostrar la página de login por defecto de Spring Security (que permite elegir entre varios proveedores si los hay).

Esto es útil si solo tienes un proveedor o quieres personalizar la experiencia de login.

No exactamente. Esa configuración:

```java
.oauth2Login(oauth2 -> oauth2
    .loginPage("/oauth2/authorization/oidc-client"))
```

está pensada para la parte de **OAuth2 Client** (aplicaciones web con usuarios interactivos), no para la parte de **Resource Server**.

- Si accedes a un recurso protegido desde un navegador (flujo interactivo), Spring Security te redirige a la página de login del Authorization Server usando el cliente `oidc-client`.
- Si accedes a un recurso protegido como Resource Server (por ejemplo, desde Postman o una API externa con un JWT), **no hay redirección a login**. El Resource Server solo valida el token JWT recibido en la cabecera Authorization y responde 401 si no es válido o no está presente.

En resumen:
- `.oauth2Login()` → flujo interactivo (web, navegador, login)
- `.oauth2ResourceServer()` → flujo API (validación de JWT, sin login interactivo)

Ambos pueden convivir, pero la redirección a login solo aplica para usuarios web, no para llamadas API con JWT.

## Aclaración importante acerca de oauth2/authorization/{registrationId}

### Inicio del flujo autenticación Oauth2/OIDC de forma explicita

Cuando accedes a `/oauth2/authorization/oidc-client` desde el navegador, **estás accediendo directamente al endpoint interno de Spring Security que inicia el flujo de autenticación OAuth2/OIDC** para el cliente con `registrationId` igual a `oidc-client`.  
Esto significa que, al visitar esa URL, Spring Security inmediatamente redirige tu navegador al Authorization Server (por ejemplo, la pantalla de login del proveedor) sin pasar por ningún otro filtro o lógica de tu aplicación.

**En resumen:**  
- **/oauth2/authorization/oidc-client**:  
  - Es un endpoint especial de Spring Security.
  - Su único propósito es iniciar el flujo de login OAuth2/OIDC para ese cliente.
  - Si tienes varios proveedores, puedes tener `/oauth2/authorization/google`, `/oauth2/authorization/github`, etc.
  - Es útil, por ejemplo, para poner un botón "Iniciar sesión con Google" que apunte directamente a `/oauth2/authorization/google`.

### Inicio del flujo autenticación Oauth2/OIDC de forma implicita

- **Cualquier otro endpoint protegido**:  
  - Si accedes a `/hello`, `/api/books`, etc., y no estás autenticado, Spring Security detecta que necesitas autenticación y **te redirige automáticamente** a la página de login configurada (que puede ser `/oauth2/authorization/oidc-client` si así lo definiste).
  - Aquí, la redirección ocurre como consecuencia de intentar acceder a un recurso protegido, no porque tú hayas solicitado explícitamente el inicio de sesión.

**Diferencia clave:**  
- Acceder a `/oauth2/authorization/oidc-client` es como decirle a la app: "¡Quiero iniciar sesión ahora mismo con este proveedor!"  
- Acceder a otro endpoint protegido y no estar autenticado es: "Quiero acceder a este recurso, pero como no estoy autenticado, la app me obliga a iniciar sesión."

Ambos caminos terminan en el login, pero uno es directo (explícito) y el otro es indirecto (automático por protección de recursos).

¡Muy buena pregunta!  
Sí, cuando accedes a cualquier endpoint protegido sin estar autenticado, **Spring Security internamente utiliza el endpoint `/oauth2/authorization/{registrationId}`** para iniciar el flujo de login.

Es decir, aunque tú no accedas explícitamente a `/oauth2/authorization/oidc-client`, Spring Security redirige tu navegador a ese endpoint especial para arrancar el proceso de autenticación con el proveedor correspondiente.

**En resumen:**  
- Si accedes directamente a `/oauth2/authorization/oidc-client`, inicias el login explícitamente.
- Si accedes a otro endpoint protegido, Spring Security te redirige automáticamente a `/oauth2/authorization/oidc-client` (o el que corresponda), y desde ahí comienza el flujo de login.

==Ambos caminos terminan usando ese endpoint interno para arrancar la autenticación OAuth2/OIDC.==