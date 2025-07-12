# Aplicación Cliente

Este proyecto es una aplicación Spring Boot que demuestra el uso de OAuth2 para autenticación y autorización. Proporciona una API REST simple para administrar una colección de libros y cumple un doble rol en el ecosistema OAuth2.

## Doble rol OAuth2: Cliente y Resource Server

Esta aplicación desempeña dos roles clave en el ecosistema OAuth2:

### 1. OAuth2 Client
Como cliente OAuth2, la aplicación hace:
- Redirige a los usuarios al servidor de autorización (en `127.0.0.1:9000`) cuando intentan acceder a recursos protegidos sin autenticación.
- Obtiene tokens de acceso del servidor de autorización tras el login exitoso del usuario.
- Almacena los tokens para futuras peticiones y los renueva cuando expiran.
- Permite a los usuarios finales autenticarse usando sus credenciales en el servidor de autorización sin exponer esas credenciales a esta aplicación.

### 2. OAuth2 Resource Server
Como servidor de recursos, la aplicación:
- Protege todos sus endpoints REST para que solo sean accesibles con tokens JWT válidos.
- Valida que los tokens presentados hayan sido emitidos por el servidor de autorización confiable.
- Autoriza el acceso a los endpoints según los scopes del token:
  - Endpoints de lectura (`GET`): requieren scope `read` o `write`.
  - Endpoints de escritura (`POST`, `PUT`, `DELETE`): requieren scope `write`.
- Rechaza peticiones con tokens inválidos, expirados o con scopes insuficientes.

## Tecnologías Utilizadas

* Java 21
* Spring Boot
* Spring Security
* OAuth2 Client
* OAuth2 Resource Server
* Spring Web
* Spring Data JPA
* MySQL

## Estructura del Proyecto

El proyecto está estructurado de la siguiente manera:

* `src/main/java`: Contiene el código fuente principal de la aplicación.
    * `com.jfuente040.oauth2client.client_app`: El paquete raíz de la aplicación.
        * `ClientAppApplication.java`: El punto de entrada principal de la aplicación.
        * `config`: Contiene la configuración de seguridad de la aplicación.
        * `controller`: Contiene los controladores REST de la aplicación.
        * `persistence`: Contiene la capa de persistencia de la aplicación (entidades y repositorios).
        * `service`: Contiene la lógica de negocio de la aplicación.
* `src/main/resources`: Contiene los recursos de la aplicación.
    * `application.properties`: Contiene la configuración de la aplicación.
    * `application.yml`: Contiene la configuración de la aplicación.
* `pom.xml`: Contiene las dependencias y la configuración de compilación de la aplicación.

## Configuración

1. **Clona el repositorio:**

```bash
git clone https://github.com/jfuente040/oauth2.git
```

2. **Configura la aplicación:**

La aplicación se puede configurar usando el archivo `application.properties` o `application.yml`. Es necesario configurar las siguientes propiedades:

* **Datasource:**
    * `spring.datasource.url`: La URL de la base de datos MySQL.
    * `spring.datasource.username`: El nombre de usuario de la base de datos MySQL.
    * `spring.datasource.password`: La contraseña de la base de datos MySQL.
* **OAuth2 Client:**
    * `spring.security.oauth2.client.registration.oidc-client.client-id`: El ID de cliente registrado en el servidor de autorización.
    * `spring.security.oauth2.client.registration.oidc-client.client-secret`: El secreto del cliente.
    * `spring.security.oauth2.client.registration.oidc-client.authorization-grant-type`: El tipo de concesión (normalmente "authorization_code").
    * `spring.security.oauth2.client.registration.oidc-client.scope`: Los ámbitos solicitados (openid, profile, read, write).
    * `spring.security.oauth2.client.registration.oidc-client.redirect-uri`: La URI de redirección tras el login exitoso.
* **OAuth2 Provider:**
    * `spring.security.oauth2.client.provider.spring-provider.authorization-uri`: URL del endpoint de autorización.
    * `spring.security.oauth2.client.provider.spring-provider.token-uri`: URL del endpoint de token.
    * `spring.security.oauth2.client.provider.spring-provider.user-info-uri`: URL del endpoint de información del usuario.
    * `spring.security.oauth2.client.provider.spring-provider.jwk-set-uri`: URL del endpoint de JWK para validar tokens.
* **Resource Server:**
    * `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`: URL del endpoint de JWK para validar tokens.

3. **Ejecuta la aplicación:**

```bash
mvn spring-boot:run
```

## Uso

### Interacción con la aplicación

La aplicación se puede usar de dos maneras:

1. **A través del navegador (como OAuth2 Client):**
   - Acceda a cualquier endpoint protegido desde el navegador.
   - Será redirigido automáticamente al servidor de autorización para iniciar sesión.
   - Tras un login exitoso, volverá a la aplicación con un token válido.

2. **A través de herramientas como Postman (como Resource Server):**
   - Primero obtenga un token válido del servidor de autorización.
   - Incluya el token en el encabezado de autorización: `Authorization: Bearer <token>`.
   - Las peticiones sin token válido serán rechazadas con código 401.
   - Las peticiones con un token que carezca de los scopes necesarios serán rechazadas con código 403.

### Endpoints disponibles:

* `POST /api/books`: Crea un nuevo libro. Requiere scope `write`.
* `GET /api/books`: Devuelve una lista de todos los libros. Requiere scope `read` o `write`.
* `GET /api/books/{id}`: Devuelve un libro por su ID. Requiere scope `read` o `write`.
* `PUT /api/books/{id}`: Actualiza un libro por su ID. Requiere scope `write`.
* `DELETE /api/books/{id}`: Elimina un libro por su ID. Requiere scope `write`.

### Ejemplos de solicitud JSON

Para crear o actualizar un libro (`POST /api/books` o `PUT /api/books/{id}`):

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884"
}
```

```json
{
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "isbn": "9780134685991"
}
```

## Licencia

Este proyecto está licenciado bajo la Licencia MIT.