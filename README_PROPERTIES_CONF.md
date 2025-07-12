
## {baseUrl} y {registrationId}

En la propiedad `redirect-uri`, los valores `{baseUrl}` y `{registrationId}` son placeholders que Spring Boot reemplaza automáticamente:

- `{baseUrl}`: Es la URL base de tu aplicación cliente (por ejemplo, `http://localhost:8080`). Spring la determina en tiempo de ejecución según la URL con la que accedes a la app.
- `{registrationId}`: Es el identificador de registro del cliente OAuth2, que en tu configuración es `my-client`. (spring.security.oauth2.client.registration.my-client)
Spring lo usa para distinguir entre diferentes clientes configurados.

No necesitas asignarlos manualmente; Spring los resuelve automáticamente cuando se inicia el flujo OAuth2. Si necesitas un valor específico para `baseUrl`, puedes configurarlo con la propiedad `server.servlet.context-path` o asegurarte de acceder a la app con la URL deseada.


## ¿Necesitamos crear el endpoint para recibir el codigo de autorización?

No es necesario definir manualmente la URI de redirección en el backend ni crear un endpoint personalizado para recibir el código de autorización si usas la configuración estándar de Spring Security.

Spring Boot, al usar `{baseUrl}/login/oauth2/code/{registrationId}` como `redirect-uri`, ya expone automáticamente el endpoint `/login/oauth2/code/oidc-client` (donde `oidc-client` es tu `registrationId`).  
Spring Security intercepta esa ruta, recibe el código de autorización y gestiona el intercambio por el token, sin que debas crear un controlador propio.

Solo necesitas asegurarte de que:
- El `redirect-uri` registrado en tu Authorization Server coincida exactamente con el que usas en la app.
- El navegador pueda acceder a esa ruta (no esté bloqueada por firewall, proxy, etc.).

Si necesitas lógica personalizada tras el login, puedes implementar un `AuthenticationSuccessHandler` o un filtro, pero no es obligatorio para el flujo estándar.

## Propiedad `user-info-uri`

La propiedad `user-info-uri` indica la URL del endpoint donde el cliente puede obtener información del usuario autenticado (por ejemplo, nombre, email, etc.) usando el token de acceso.

En el flujo de autorización con OpenID Connect u OAuth2, después de obtener el token, Spring Security hace una petición a esa URL para recuperar los datos del usuario y construir el principal de autenticación en la aplicación.

No es necesaria en el flujo **client credentials**, pero sí en los flujos donde hay autenticación de usuario (como `authorization_code`).

---

## Personalizar la uri de redirección  para recibir el codigo de autorización

El valor de `redirect-uri` en la configuración OAuth2 indica la URL a la que el Authorization Server enviará el código de autorización tras el login exitoso.

### Opción estándar de Spring Security
- `{baseUrl}/login/oauth2/code/{registrationId}`  
  Es la convención de Spring Security.  
  ==Spring intercepta automáticamente esta ruta y gestiona el flujo OAuth2 sin que debas crear un endpoint manual.==

### Opción personalizada: `{baseUrl}/callback`
- Puedes usar `{baseUrl}/callback` o cualquier otra ruta personalizada como redirect-uri.
- **Pero:**  
  Si usas una ruta personalizada, **debes crear un endpoint en tu aplicación** que reciba el código de autorización y gestione el intercambio por el token, ya que Spring Security solo intercepta automáticamente la ruta estándar.

#### Ejemplo de configuración personalizada:
```yaml
redirect-uri: "{baseUrl}/callback"
```
Y en tu backend deberías tener algo como:
```java
@GetMapping("/callback")
public String callback(@RequestParam String code) {
    // Aquí deberías intercambiar el código por el token manualmente
    // o delegar a un servicio que lo haga
}
```

### Resumen
- Usa la ruta estándar (`/login/oauth2/code/{registrationId}`) si quieres que Spring gestione todo automáticamente.
- Usa una ruta personalizada (como `/callback`) solo si necesitas lógica especial y estás dispuesto a implementar el manejo del código de autorización tú mismo.

==**En ambos casos, la URI debe coincidir exactamente con la registrada en el Authorization Server.**==