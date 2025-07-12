## En el cliente Oauth2

### ¿Tiene sentido tener varias URIs de redirección para un mismo cliente?

No, **no tiene sentido** definir dos propiedades `redirect-uri` para el mismo cliente en application.yml.  
Spring Boot solo tomará una de ellas (generalmente la última), y la otra será ignorada.

**Lo correcto es definir solo una URI de redirección** por cliente. Esa URI debe coincidir exactamente con la registrada en el Authorization Server para ese cliente.

Si necesitas soportar varias URIs de redirección (por ejemplo, para distintos entornos), debes registrarlas todas en el Authorization Server, pero en la configuración de tu aplicación solo debes poner una por cliente o usar perfiles/configuración dinámica para cambiarla según el entorno.



## En el Servidor de Autorización

### ¿Tiene sentido tener varias URIs de redirección ?

Sí, **tiene sentido tener varias URIs de redirección** registradas para un mismo cliente. Esto es útil cuando:

- Tienes **diferentes entornos** (desarrollo, pruebas, producción), cada uno con su propia URL.
- Tu aplicación cliente puede ejecutarse en diferentes dominios o puertos (por ejemplo, localmente y en la nube).
- Soportas diferentes tipos de clientes (web, móvil, etc.) que requieren distintas URIs de redirección.

Por ejemplo, en tu configuración:

```java
.redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
.redirectUri("https://127.0.1:8080/authorized")
```

Esto permite que el Authorization Server acepte redirigir a cualquiera de esas dos URIs, según cuál haya sido usada en la petición de autorización.  
**Solo debes asegurarte de que el cliente realmente pueda recibir la respuesta en esas rutas.**  
Esto es una práctica común y recomendada para facilitar el desarrollo y despliegue en distintos entornos.